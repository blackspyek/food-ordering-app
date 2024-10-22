package com.food.backend.dto;

import com.food.backend.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Schema(description = "Data Transfer Object for User")
public class UserDto {
    @Schema(description = "Username of the user", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name cannot be empty")
    private String userName;

    @Schema(description = "Roles assigned to the user")
    private Set<Role> roles;

}
