package com.food.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data transfer object for user registration")
public class RegisterUserDto {
    @Schema(description = "Email for the new account", example = "johndoe@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "Name for the new account", example = "john", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Schema(description = "Phone number for the new account", example = "+1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone number is mandatory")
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number format is invalid"
    )
    private String phone;

    @Schema(description = "Password for the new account", example = "securePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 60, message = "Password must be at least 6 characters long")
    private String password;

}
