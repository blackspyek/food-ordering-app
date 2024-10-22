package com.food.backend.controller;

import com.food.backend.dto.UserDto;
import com.food.backend.model.User;
import com.food.backend.service.UserService;

import com.food.backend.utils.classes.ApiResponse;
import com.food.backend.utils.classes.MessageUtil;
import com.food.backend.utils.classes.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
@Tag(name = "User Management", description = "APIs for managing users and user profiles")
public class UserController {

    private final UserService userService;


    UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get authenticated user",
            description = "Retrieves the currently authenticated user's profile"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved authenticated user",
            content = @Content(schema = @Schema(implementation = User.class))
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }


    @Operation(
            summary = "Find user by ID",
            description = "Retrieves a specific user by their ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> findUserById(@PathVariable Long id) {
        return userService.findUserById(id)
                .map(user -> ResponseUtil.successResponse(user, "User found"))
                .orElseGet(() -> ResponseUtil.notFoundResponse(MessageUtil.userNotFoundMessage(id)));
    }


    @Operation(
            summary = "Delete user",
            description = "Deletes a user by their ID. Requires MANAGER role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User successfully deleted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Requires MANAGER role")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<User>> deleteUserById(@PathVariable Long id) {
        return userService.deleteUserById(id)
                .map(user -> ResponseUtil.successResponse(user, MessageUtil.userDeletedMessage(id)))
                .orElseGet(() -> ResponseUtil.notFoundResponse(MessageUtil.userNotFoundMessage(id)));
    }

    @Operation(
            summary = "Update user",
            description = "Updates a user's information by their ID. Requires MANAGER role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User successfully updated"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input - Roles cannot be empty"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied - Requires MANAGER role")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<User>> updateUserById(@PathVariable Long id,
                                                            @Valid
                                                            @RequestBody UserDto userDto){
        if (!UserService.hasRoles(userDto)) return ResponseUtil.badRequestResponse("Roles cannot be empty");
        return userService.updateUserById(id, userDto)
                .map(user -> ResponseUtil.successResponse(user, MessageUtil.userUpdatedMessage(id)))
                .orElseGet(() -> ResponseUtil.notFoundResponse(MessageUtil.userNotFoundMessage(id)));
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves all users in the system. Requires MANAGER role."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all users",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = User.class)))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied - Requires MANAGER role"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<User>>> allUsers() {
        List<User> users = userService.allUsers();
        return ResponseUtil.successResponse(users, "All users retrieved");
    }

}
