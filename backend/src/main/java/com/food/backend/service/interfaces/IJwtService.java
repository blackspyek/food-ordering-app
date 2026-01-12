package com.food.backend.service.interfaces;

import com.food.backend.model.User;
import io.jsonwebtoken.Claims;

import java.util.Map;
import java.util.function.Function;

public interface IJwtService {
    String extractEmail(String token);
    boolean isTokenExpired(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    String generateToken(User userDetails);
    String generateToken(Map<String, Object> extraClaims, User userDetails);
    boolean isTokenValid(String token, User userDetails);
    long getJwtExpirationTime();
}
