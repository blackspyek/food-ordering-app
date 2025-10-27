package com.food.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data transfer object for user login")
public class LoginUserDto {
    @Schema(description = "Email for login", example = "johndoe@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "Password for login", example = "securePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}
