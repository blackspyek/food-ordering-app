package com.food.backend.controller;

import com.food.backend.dto.LoginUserDto;
import com.food.backend.dto.RegisterUserDto;
import com.food.backend.model.Role;
import com.food.backend.model.User;
import com.food.backend.responses.LoginResponse;
import com.food.backend.service.AuthenticationService;
import com.food.backend.service.JwtService;
import com.food.backend.utils.classes.ApiResponse;
import com.food.backend.utils.classes.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
@Tag(name = "Authentication", description = "Authentication management APIs")
@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody RegisterUserDto registerUserDto) {
        try {
            return authenticationService.registerUser(registerUserDto)
                    .map(user -> ResponseUtil.successResponse(user, "User registered"))
                    .orElseGet(() -> ResponseUtil.badRequestResponse("Registration failed"));
        } catch (IllegalArgumentException e) {
            return ResponseUtil.badRequestResponse(e.getMessage());
        }
    }

    @Operation(summary = "Authenticate a user", description = "Authenticates a user and returns a JWT token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid username or password",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> authenticate(@RequestBody LoginUserDto loginUserDto) {
        try{
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            String token = jwtService.generateToken(authenticatedUser);
            LoginResponse loginResponse = new LoginResponse(token, jwtService.getJwtExpirationTime(), authenticatedUser.getRoles());
            return ResponseUtil.successResponse(loginResponse, "Login successful");
        }
        catch (UsernameNotFoundException e) {
            return ResponseUtil.badRequestResponse("Invalid username or password");
        }
    }

    @Operation(summary = "Change user roles", description = "Updates the roles for a given user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User roles updated successfully",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/patch/roles/{username}")
    public ResponseEntity<User> changeRoles(@PathVariable String username, @RequestBody Set<Role> roles) {
        Optional<User> userOptional = authenticationService.changeRoles(username, roles);
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get user details", description = "Retrieves user details for a given username")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/user/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        Optional<User> userOptional = authenticationService.findByUsername(username);
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
