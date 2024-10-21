package com.food.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.backend.dto.UserDto;
import com.food.backend.model.User;
import com.food.backend.model.Role;
import com.food.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setRoles(roles);

        testUserDto = new UserDto();
        testUserDto.setUserName("testUser");
        testUserDto.setRoles(roles);
    }


    @Test
    @WithMockUser
    void findUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        when(userService.findUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testUser"))
                .andExpect(jsonPath("$.message").value("User found"));
    }

    @Test
    @WithMockUser
    void findUserById_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(userService.findUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 999 does not exist"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteUserById_WhenUserExists_ShouldReturnSuccessMessage() throws Exception {
        when(userService.deleteUserById(1L)).thenReturn(Optional.of(testUser));

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User with id 1 has been deleted"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteUserById_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(userService.deleteUserById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 999 does not exist"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateUserById_WhenUserExistsAndDtoValid_ShouldReturnUpdatedUser() throws Exception {
        when(userService.updateUserById(eq(1L), any(UserDto.class))).thenReturn(Optional.of(testUser));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testUser"))
                .andExpect(jsonPath("$.message").value("User with id 1 has been updated"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void updateUserById_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(userService.updateUserById(eq(999L), any(UserDto.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User with id 999 does not exist"));
    }

    @Test
    @WithMockUser
    void updateUserById_WhenDtoHasNoRoles_ShouldReturnForbidden() throws Exception {
        testUserDto.setRoles(new HashSet<>());

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void allUsers_ShouldReturnListOfUsers() throws Exception {
        when(userService.allUsers()).thenReturn(Collections.singletonList(testUser));

        mockMvc.perform(get("/users/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].username").value("testUser"))
                .andExpect(jsonPath("$.message").value("All users retrieved"));
    }

    @Test
    @WithMockUser
    void allUsers_ShouldReturnPermissionErrorOfListOfUsers() throws Exception {
        when(userService.allUsers()).thenReturn(Collections.singletonList(testUser));

        mockMvc.perform(get("/users/"))
                .andExpect(status().isForbidden());
    }
}