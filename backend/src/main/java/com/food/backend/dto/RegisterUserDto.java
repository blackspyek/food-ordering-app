package com.food.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data transfer object for user registration")
public class RegisterUserDto {
    @Schema(description = "Username for the new account", example = "johndoe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Password for the new account", example = "securePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
