package com.food.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data transfer object for user login")
public class LoginUserDto {
    @Schema(description = "Username for login", example = "johndoe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @Schema(description = "Password for login", example = "securePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
