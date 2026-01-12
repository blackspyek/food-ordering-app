package com.food.backend.service;

import com.food.backend.dto.UserDto;
import com.food.backend.model.User;
import com.food.backend.repository.IUserRepository;
import com.food.backend.service.interfaces.IUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements IUserService {
    private final IUserRepository IUserRepository;
    public UserService(IUserRepository IUserRepository) {
        this.IUserRepository = IUserRepository;
    }
    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        IUserRepository.findAll().forEach(users::add);
        return users;
    }

    public Optional<User> findUserById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return IUserRepository.findById(id);
    }

    public User findUserByUsername(String username) {
        return IUserRepository.findByEmail(username).orElseThrow(() -> new EntityNotFoundException("User with username " + username + " not found"));
    }
    public Optional<User> deleteUserById(Long id) {
        Optional<User> user = IUserRepository.findById(id);
        IUserRepository.deleteById(id);
        return user;
    }
    public Optional<User> updateUserById(Long id, UserDto userDto) {
        Optional<User> user = IUserRepository.findById(id);
        user.ifPresent(value -> updateFieldsOfUserEntityWithUserDto(userDto, value));
        return user;
    }
    private void updateFieldsOfUserEntityWithUserDto(UserDto userDto, User userToUpdate) throws EntityNotFoundException {
        userToUpdate.setEmail(userDto.getEmail());
        userToUpdate.setRoles(userDto.getRoles());
        IUserRepository.save(userToUpdate);
    }

    public static Boolean hasRoles(UserDto userDto) {
        return userDto.getRoles() != null;
    }


}
