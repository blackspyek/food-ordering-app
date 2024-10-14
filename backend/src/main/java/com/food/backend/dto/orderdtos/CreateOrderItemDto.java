package com.food.backend.dto.orderdtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderItemDto {
    @NotNull(message = "Menu item id cannot be null")
    private Long menuItemId;

    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
}
