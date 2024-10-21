package com.food.backend.services;

import com.food.backend.model.User;
import com.food.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class JwtServiceTests {

    @Autowired
    private JwtService jwtService;

    @MockBean
    private User user;


    private String token;

    @BeforeEach
    void setUp() {
        when(user.getUsername()).thenReturn("testUser");
        when(user.getId()).thenReturn(1L);
        when(user.isEnabled()).thenReturn(true);

        // Generate a token for testing
        token = jwtService.generateToken(user);
    }

    @Test
    void testExtractUsername() {
        String username = jwtService.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    void testGenerateToken() {
        User anotherUser = mock(User.class);
        when(anotherUser.getUsername()).thenReturn("testUserr");
        when(anotherUser.getId()).thenReturn(2L);
        when(anotherUser.isEnabled()).thenReturn(true);
        String generatedToken = jwtService.generateToken(anotherUser);
        assertNotNull(generatedToken);
        assertNotEquals(token, generatedToken);
    }

    @Test
    void testIsTokenValid() {
        boolean isValid = jwtService.isTokenValid(token, user);
        assertTrue(isValid);
    }

    @Test
    void testExtractClaim() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("testClaim", "testValue");

        // Add claim to the token
        String tokenWithClaim = jwtService.generateToken(claims, user);
        String claimValue = jwtService.extractClaim(tokenWithClaim, claimsMap -> claimsMap.get("testClaim", String.class));

        assertEquals("testValue", claimValue);
    }

    @Test
    void testGetJwtExpirationTime() {
        long expirationTime = jwtService.getJwtExpirationTime();
        assertEquals(28800000, expirationTime); // Ensure expiration time is as set in properties
    }

}
