package com.food.backend.responses;

import com.food.backend.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private long expiresIn;
    private Set<Role> roles;

    public LoginResponse(String token, long expiresIn, Set<Role> roles) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.roles = roles;

    }
}

