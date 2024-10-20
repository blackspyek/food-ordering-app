package com.food.backend.service;

import com.food.backend.dto.UserDto;
import com.food.backend.model.User;
import com.food.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User with username " + username + " not found"));
    }
    public Optional<User> deleteUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        userRepository.deleteById(id);
        return user;
    }
    public Optional<User> updateUserById(Long id, UserDto userDto) {
        Optional<User> user = userRepository.findById(id);
        user.ifPresent(value -> updateFieldsOfUserEntityWithUserDto(userDto, value));
        return user;
    }
    private void updateFieldsOfUserEntityWithUserDto(UserDto userDto, User userToUpdate) throws EntityNotFoundException {
        userToUpdate.setUsername(userDto.getUserName());
        userToUpdate.setRoles(userDto.getRoles());
        userRepository.save(userToUpdate);
    }

    public static Boolean hasRoles(UserDto userDto) {
        return userDto.getRoles() != null;
    }


}
