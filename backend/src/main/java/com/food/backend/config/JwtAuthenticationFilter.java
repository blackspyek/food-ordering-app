package com.food.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.backend.model.User;
import com.food.backend.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Authentication Filter for handling JWT-based authentication in Spring Security.
 * Intercepts requests, validates JWT tokens, and sets authentication context.
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Extract JWT from request
        String jwt = extractJwtFromRequest(request);

        // If no JWT, continue filter chain
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            authenticateWithJwt(jwt, request);
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired: {}", e.getMessage());
            sendErrorResponse(response, "Token has expired", HttpServletResponse.SC_UNAUTHORIZED);

        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            sendErrorResponse(response, "Invalid token", HttpServletResponse.SC_UNAUTHORIZED);

        } catch (UsernameNotFoundException e) {
            log.warn("User not found: {}", e.getMessage());
            sendErrorResponse(response, "User not found", HttpServletResponse.SC_UNAUTHORIZED);

        } catch (Exception e) {
            log.error("Authentication error", e);
            sendErrorResponse(response, "Authentication failed", HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * Extracts JWT token from Authorization header
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length()).trim();
        }

        return null;
    }

    /**
     * Authenticates user with JWT token
     */
    private void authenticateWithJwt(String jwt, HttpServletRequest request) {

        // Skip if already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        // Extract email from token (will throw exception if expired/invalid)
        String email = jwtService.extractEmail(jwt);

        if (email == null) {
            throw new JwtException("Token does not contain valid email");
        }

        // Load user details
        User user = (User) userDetailsService.loadUserByUsername(email);

        // Validate token
        if (!jwtService.isTokenValid(jwt, user)) {
            throw new JwtException("Token validation failed");
        }

        // Set authentication in security context
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);

    }

    /**
     * Sends JSON error response
     */
    private void sendErrorResponse(
            HttpServletResponse response,
            String message,
            int statusCode
    ) throws IOException {

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", statusCode);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", message);
        errorDetails.put("timestamp", System.currentTimeMillis());

        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorDetails);
    }
}