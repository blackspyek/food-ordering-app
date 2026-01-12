package com.food.backend.service.interfaces;

import com.food.backend.dto.LoginUserDto;
import com.food.backend.dto.RegisterUserDto;
import com.food.backend.model.Role;
import com.food.backend.model.User;

import java.util.Optional;
import java.util.Set;

public interface IAuthenticationService {
    Optional<User> registerUser(RegisterUserDto registerUserDto);
    Optional<User> changeRoles(String email, Set<Role> roles);
    User authenticate(LoginUserDto loginUserDto);
    Optional<User> findByEmail(String email);
    String generatePasswordResetToken(String email);
    boolean resetPassword(String token, String newPassword);
}
