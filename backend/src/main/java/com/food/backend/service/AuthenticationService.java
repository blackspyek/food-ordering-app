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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
        validateUserDoesNotExist(registerUserDto.getEmail());

        User user = createUser(registerUserDto);
        User savedUser = userRepository.save(user);

        return Optional.of(savedUser);
    }
    private void validateUserDoesNotExist(String email) {
        if (findByEmail(email.toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
    }

    private User createUser(RegisterUserDto registerUserDto) {
        User user = new User();
        user.setEmail(registerUserDto.getEmail().toLowerCase());
        user.setName(registerUserDto.getName());
        user.setPhoneNumber(registerUserDto.getPhone());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER));
        user.setEnabled(true);
        return user;
    }
    @Transactional
    public Optional<User> changeRoles(String email, Set<Role> roles) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setRoles(roles);
            return Optional.of(userRepository.save(user));
        }
        return Optional.empty();
    }
    public User authenticate(LoginUserDto loginUserDto) {
        User user = userRepository.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!user.isEnabled())
            throw new RuntimeException("User is disabled");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDto.getEmail(), loginUserDto.getPassword())
        );

        return user;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public String generatePasswordResetToken(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email.toLowerCase());
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        return token;
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByPasswordResetToken(token);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        if (user.getPasswordResetTokenExpiry() == null || 
            user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);

        return true;
    }

}
