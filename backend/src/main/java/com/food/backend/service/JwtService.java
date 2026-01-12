package com.food.backend.service;

import com.food.backend.model.User;
import com.food.backend.service.interfaces.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService implements IJwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    /**
     * Extracts email (subject) from JWT token
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Checks if token has expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Extracts specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates JWT token for user
     */
    public String generateToken(User userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates JWT token with extra claims
     */
    public String generateToken(Map<String, Object> extraClaims, User userDetails) {
        extraClaims.put("enabled", userDetails.isEnabled());
        extraClaims.put("id", userDetails.getId());
        extraClaims.put("name", userDetails.getName());
        extraClaims.put("roles", userDetails.getRoles().stream()
                .map(Enum::name)
                .toList());

        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Validates token against user details
     */
    public boolean isTokenValid(String token, User userDetails) {
        try {
            final String email = extractEmail(token);
            return email.equals(userDetails.getEmail()) && !isTokenExpired(token);
        } catch (JwtException e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets JWT expiration time
     */
    public long getJwtExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Builds JWT token with claims and expiration
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            User userDetails,
            long expiration
    ) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extracts all claims from token
     * @throws ExpiredJwtException if token has expired
     * @throws JwtException if token is invalid
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Gets the signing key for JWT
     */
    private SecretKey getSignInKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Error decoding secret key", e);
            throw new RuntimeException("Invalid JWT secret key configuration", e);
        }
    }
}