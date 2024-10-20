package com.food.backend.dto.orderdtos;

import com.food.backend.model.Enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePreparedByDto {
    @NotNull(message = "userName cannot be empty")
    private String userName;
}
