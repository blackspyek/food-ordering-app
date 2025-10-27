package com.food.backend.controller;

import com.food.backend.dto.ForgotPasswordDto;
import com.food.backend.dto.LoginUserDto;
import com.food.backend.dto.RegisterUserDto;
import com.food.backend.dto.ResetPasswordDto;
import com.food.backend.model.Role;
import com.food.backend.model.User;
import com.food.backend.responses.LoginResponse;
import com.food.backend.service.AuthenticationService;
import com.food.backend.service.EmailService;
import com.food.backend.service.JwtService;
import com.food.backend.utils.classes.ApiResponse;
import com.food.backend.utils.classes.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
@Tag(name = "Authentication", description = "Authentication management APIs")
@RequestMapping("/api/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, EmailService emailService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
    }
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid email or password",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {
        try{
            User authenticatedUser = authenticationService.authenticate(loginUserDto);
            String token = jwtService.generateToken(authenticatedUser);
            LoginResponse loginResponse = new LoginResponse(token, jwtService.getJwtExpirationTime(), authenticatedUser.getRoles());
            return ResponseUtil.successResponse(loginResponse, "Login successful");
        }
        catch (UsernameNotFoundException e) {
            return ResponseUtil.badRequestResponse("Invalid email or password");
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
    @PatchMapping("/patch/roles/{email}")
    public ResponseEntity<User> changeRoles(@PathVariable String email, @RequestBody Set<Role> roles) {
        Optional<User> userOptional = authenticationService.changeRoles(email, roles);
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get user details", description = "Retrieves user details for a given email")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/user/{email}")
    public ResponseEntity<User> getUser(@PathVariable String email) {
        Optional<User> userOptional = authenticationService.findByEmail(email);
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Request password reset", description = "Sends a password reset email to the user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset email sent",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {
        String token = authenticationService.generatePasswordResetToken(forgotPasswordDto.getEmail());
        
        // Always return success to prevent email enumeration
        if (token != null) {
            String resetLink = frontendUrl + "/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(forgotPasswordDto.getEmail(), resetLink);
        }
        
        return ResponseUtil.successResponse(null, "If your email exists in our system, you will receive a password reset link");
    }

    @Operation(summary = "Reset password", description = "Resets user password using a valid token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired token",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        boolean success = authenticationService.resetPassword(resetPasswordDto.getToken(), resetPasswordDto.getNewPassword());
        
        if (success) {
            return ResponseUtil.successResponse(null, "Password has been reset successfully");
        } else {
            return ResponseUtil.badRequestResponse("Invalid or expired reset token");
        }
    }

}
