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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    // temporary


    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }
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

    @PatchMapping("/patch/roles/{username}")
    public ResponseEntity<User> changeRoles(@PathVariable String username, @RequestBody Set<Role> roles) {
        Optional<User> userOptional = authenticationService.changeRoles(username, roles);
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/user/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        Optional<User> userOptional = authenticationService.findByUsername(username);
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
