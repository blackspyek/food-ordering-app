package com.food.backend.service;

import com.food.backend.dto.LoginUserDto;
import com.food.backend.dto.RegisterUserDto;
import com.food.backend.model.Role;
import com.food.backend.model.User;
import com.food.backend.repository.UserRepository;
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
    public Optional<User> registerUser(RegisterUserDto registerUserDto) {
        validateUserDoesNotExist(registerUserDto.getUsername());

        User user = createUser(registerUserDto);
        User savedUser = userRepository.save(user);

        return Optional.of(savedUser);
    }
    private void validateUserDoesNotExist(String username) {
        if (findByUsername(username.toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
    }

    private User createUser(RegisterUserDto registerUserDto) {
        User user = new User();
        user.setUsername(registerUserDto.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setRoles(Set.of(Role.ROLE_EMPLOYEE));
        user.setEnabled(true);
        return user;
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
