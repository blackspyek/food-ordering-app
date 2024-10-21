package com.food.backend.controllers;

import com.food.backend.controller.AuthenticationController;
import com.food.backend.dto.LoginUserDto;
import com.food.backend.dto.RegisterUserDto;
import com.food.backend.model.Role;
import com.food.backend.model.User;
import com.food.backend.responses.LoginResponse;
import com.food.backend.service.AuthenticationService;
import com.food.backend.service.JwtService;
import com.food.backend.utils.classes.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationService authenticationService;

    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationController = new AuthenticationController(jwtService, authenticationService);
    }

    @Test
    void register_SuccessfulRegistration() {
        // Arrange
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername("testUser");
        registerUserDto.setPassword("password123");

        User savedUser = new User();
        savedUser.setUsername("testUser");
        savedUser.setEnabled(true);
        savedUser.setRoles(Set.of(Role.ROLE_EMPLOYEE));

        when(authenticationService.registerUser(any(RegisterUserDto.class)))
                .thenReturn(Optional.of(savedUser));

        // Act
        ResponseEntity<ApiResponse<User>> response = authenticationController.register(registerUserDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User registered", response.getBody().getMessage());
        assertEquals(savedUser, response.getBody().getData());
    }

    @Test
    void register_FailedRegistration() {
        // Arrange
        RegisterUserDto registerUserDto = new RegisterUserDto();
        when(authenticationService.registerUser(any(RegisterUserDto.class)))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<User>> response = authenticationController.register(registerUserDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Registration failed", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void register_UserAlreadyExists() {
        // Arrange
        RegisterUserDto registerUserDto = new RegisterUserDto();
        when(authenticationService.registerUser(any(RegisterUserDto.class)))
                .thenThrow(new IllegalArgumentException("User already exists"));

        // Act
        ResponseEntity<ApiResponse<User>> response = authenticationController.register(registerUserDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already exists", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void authenticate_SuccessfulLogin() {
        // Arrange
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setUsername("testUser");
        loginUserDto.setPassword("password123");

        User authenticatedUser = new User();
        authenticatedUser.setUsername("testUser");
        authenticatedUser.setRoles(Set.of(Role.ROLE_EMPLOYEE));

        String token = "jwt.token.here";
        long expirationTime = 3600000L;

        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(authenticatedUser);
        when(jwtService.generateToken(authenticatedUser)).thenReturn(token);
        when(jwtService.getJwtExpirationTime()).thenReturn(expirationTime);

        // Act
        ResponseEntity<ApiResponse<Object>> response = authenticationController.authenticate(loginUserDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Login successful", response.getBody().getMessage());
        assertInstanceOf(LoginResponse.class, response.getBody().getData());

        LoginResponse loginResponse = (LoginResponse) response.getBody().getData();
        assertEquals(token, loginResponse.getToken());
        assertEquals(expirationTime, loginResponse.getExpiresIn());
        assertEquals(authenticatedUser.getRoles(), loginResponse.getRoles());
    }

    @Test
    void authenticate_InvalidCredentials() {
        // Arrange
        LoginUserDto loginUserDto = new LoginUserDto();
        when(authenticationService.authenticate(any(LoginUserDto.class)))
                .thenThrow(new UsernameNotFoundException("Invalid username or password"));

        // Act
        ResponseEntity<ApiResponse<Object>> response = authenticationController.authenticate(loginUserDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid username or password", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void changeRoles_SuccessfulUpdate() {
        // Arrange
        String username = "testUser";
        Set<Role> newRoles = new HashSet<>(Set.of(Role.ROLE_MANAGER));
        User updatedUser = new User();
        updatedUser.setUsername(username);
        updatedUser.setRoles(newRoles);

        when(authenticationService.changeRoles(username, newRoles))
                .thenReturn(Optional.of(updatedUser));

        // Act
        ResponseEntity<User> response = authenticationController.changeRoles(username, newRoles);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newRoles, response.getBody().getRoles());
    }

    @Test
    void changeRoles_UserNotFound() {
        // Arrange
        String username = "nonexistentUser";
        Set<Role> newRoles = new HashSet<>();
        when(authenticationService.changeRoles(username, newRoles))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = authenticationController.changeRoles(username, newRoles);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getUser_UserExists() {
        // Arrange
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        when(authenticationService.findByUsername(username))
                .thenReturn(Optional.of(user));

        // Act
        ResponseEntity<User> response = authenticationController.getUser(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(username, response.getBody().getUsername());
    }

    @Test
    void getUser_UserNotFound() {
        // Arrange
        String username = "nonexistentUser";
        when(authenticationService.findByUsername(username))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = authenticationController.getUser(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
