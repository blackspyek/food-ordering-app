/**
 * JWT Authentication Filter for handling JWT-based authentication in Spring Security.
 * <p>
 * This filter intercepts incoming HTTP requests to validate JWT tokens and authenticate users.
 * It extends OncePerRequestFilter to ensure the filter is only executed once per request.
 * <p>
 * The filter performs the following operations:
 * 1. Extracts JWT token from the Authorization header
 * 2. Validates the token and extracts user information
 * 3. Creates and sets the authentication context if the token is valid
 * 4. Handles various authentication errors with appropriate HTTP responses
 *
 * @author Your Name
 * @version 1.0
 * @see OncePerRequestFilter
 * @see JwtService
 * @see UserDetailsService
 */
package com.food.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.backend.model.User;
import com.food.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Logger LOGGER = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new JwtAuthenticationFilter with the required services.
     *
     * @param jwtService Service for JWT operations
     * @param userDetailsService Service for user details operations
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Processes the incoming request for JWT authentication.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Filter chain to execute
     * @throws IOException if there's an I/O error
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException {
        try {
            String jwt = extractJwtFromRequest(request);
            if (jwt == null) {
                filterChain.doFilter(request, response);
                return;
            }

            processJwtAuthentication(jwt, request, response, filterChain);
        } catch (Exception e) {
            LOGGER.severe("JWT Authentication error: " + e.getMessage());
            sendErrorResponse(response, "Authentication failed: " + e.getMessage()
            );
        }
    }

    /**
     * Extracts JWT token from the Authorization header.
     *
     * @param request HTTP request
     * @return JWT token or null if not present
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authHeader.substring(BEARER_PREFIX.length()).trim();
    }

    /**
     * Processes JWT authentication and sets security context if valid.
     *
     * @param jwt JWT token
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain Filter chain
     * @throws IOException if there's an I/O error
     * @throws ServletException if there's a servlet error
     */
    private void processJwtAuthentication(
            String jwt,
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, ServletException {
        String username = jwtService.extractUsername(jwt);
        if (!isValidAuthenticationContext(username)) {
            sendErrorResponse(response, "Invalid Token");
            return;
        }

        User user = (User) userDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(jwt, user)) {
            sendErrorResponse(response, "Token has expired");
            return;
        }

        setAuthenticationContext(user, request);
        filterChain.doFilter(request, response);
    }

    /**
     * Checks if the authentication context is valid for processing.
     *
     * @param username Extracted username from JWT
     * @return true if context is valid for processing
     */
    private boolean isValidAuthenticationContext(String username) {
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        return username != null && existingAuth == null;
    }

    /**
     * Sets the authentication context with the validated user details.
     *
     * @param user Authenticated user
     * @param request HTTP request
     */
    private void setAuthenticationContext(User user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    /**
     * Sends error response with specified details.
     *
     * @param response HTTP response
     * @param message  Error message
     * @throws IOException if there's an I/O error
     */
    private void sendErrorResponse(HttpServletResponse response, String message)
            throws IOException {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", message);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorDetails);
    }
}