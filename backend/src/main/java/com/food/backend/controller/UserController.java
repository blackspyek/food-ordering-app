package com.food.backend.controller;

import com.food.backend.dto.UserDto;
import com.food.backend.model.User;
import com.food.backend.service.UserService;

import com.food.backend.utils.classes.ApiResponse;
import com.food.backend.utils.classes.MessageUtil;
import com.food.backend.utils.classes.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;


    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> findUserById(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(user -> ResponseUtil.successResponse(user, "User found"))
                .orElseGet(() -> ResponseUtil.notFoundResponse(MessageUtil.userFoundMessage(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> deleteUserById(@PathVariable Long id) {
        return userService.deleteUserById(id)
                .map(user -> ResponseUtil.successResponse(user, MessageUtil.userDeletedMessage(id)))
                .orElseGet(() -> ResponseUtil.notFoundResponse(MessageUtil.userNotFoundMessage(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUserById(@PathVariable Long id,
                                                            @Valid
                                                            @RequestBody UserDto userDto){
        if (!UserService.hasRoles(userDto)) return ResponseUtil.badRequestResponse("Roles cannot be empty");
        return userService.updateUserById(id, userDto)
                .map(user -> ResponseUtil.successResponse(user, MessageUtil.userUpdatedMessage(id)))
                .orElseGet(() -> ResponseUtil.notFoundResponse(MessageUtil.userNotFoundMessage(id)));
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<User>>> allUsers() {
        List<User> users = userService.allUsers();
        return ResponseUtil.successResponse(users, "All users retrieved");
    }

}
