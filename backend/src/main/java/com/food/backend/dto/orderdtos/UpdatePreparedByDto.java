package com.food.backend.dto.orderdtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for updating the employee who prepared the order")
public class UpdatePreparedByDto {
    @NotNull(message = "userName cannot be empty")
    @Schema(description = "Username of the employee who prepared the order", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;
}
