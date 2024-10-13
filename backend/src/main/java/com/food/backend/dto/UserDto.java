package com.food.backend.dto;

import com.food.backend.model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDto {
    @NotBlank(message = "Name cannot be empty")
    private String userName;

    private Set<Role> roles;

}
