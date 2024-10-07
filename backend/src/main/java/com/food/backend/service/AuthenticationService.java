package com.food.backend.service;

import com.food.backend.dto.LoginUserDto;
import com.food.backend.dto.RegisterUserDto;
import com.food.backend.model.Role;
import com.food.backend.model.User;
import com.food.backend.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public User signup(RegisterUserDto registerUserDto) {
        User user = new User();
        user.setUsername(registerUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setEnabled(true);
        return userRepository.save(user);
    }
    @Transactional
    public Optional<User> changeRoles(String username, Set<Role> roles) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRoles(roles);
            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
    }
    public User authenticate(LoginUserDto loginUserDto) {
        User user = userRepository.findByUsername(loginUserDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!user.isEnabled())
            throw new RuntimeException("User is disabled");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDto.getUsername(), loginUserDto.getPassword())
        );

        return user;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }



}
