package com.food.backend.dto.orderdtos;

import com.food.backend.model.Enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusDto {
    @NotNull(message = "OrderStatus cannot be empty")
    private OrderStatus orderStatus;
}
