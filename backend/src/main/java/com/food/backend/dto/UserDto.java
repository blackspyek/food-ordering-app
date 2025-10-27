package com.food.backend.dto;

import com.food.backend.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Schema(description = "Data Transfer Object for User")
public class UserDto {
    @Schema(description = "Email for the new account", example = "johndoe@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "Name for the new account", example = "john", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Schema(description = "Phone number for the new account", example = "+1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number format is invalid"
    )
    private String phone;

    @Schema(description = "Password for the new account", example = "securePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 60, message = "Password must be at least 6 characters long")
    private String password;

    @Schema(description = "Roles assigned to the user")
    private Set<Role> roles;

}
