package com.food.backend.dto.orderdtos;

import com.food.backend.model.Enums.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderDto {
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Order type cannot be null")
    private OrderType orderType;

    @NotEmpty(message = "Order items list cannot be empty")
    @Valid
    private List<CreateOrderItemDto> orderItems;
}