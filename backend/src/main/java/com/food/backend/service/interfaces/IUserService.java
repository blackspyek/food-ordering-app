package com.food.backend.service.interfaces;

import com.food.backend.dto.UserDto;
import com.food.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> allUsers();
    Optional<User> findUserById(Long id);
    User findUserByUsername(String username);
    Optional<User> deleteUserById(Long id);
    Optional<User> updateUserById(Long id, UserDto userDto);
}
