package com.food.backend.responses;

import com.food.backend.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Schema(description = "Login response containing JWT token and user details")
public class LoginResponse {
    @Schema(description = "JWT token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    @Schema(description = "Token expiration time in milliseconds", example = "3600000")
    private long expiresIn;
    @Schema(description = "User roles")
    private Set<Role> roles;

    public LoginResponse(String token, long expiresIn, Set<Role> roles) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.roles = roles;

    }
}

