package com.food.backend.dto.orderdtos;

import com.food.backend.model.Enums.OrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "DTO for creating a new order")
public class CreateOrderDto {
    @Email(message = "Email should be valid")
    @Schema(description = "Email of the customer", example = "customer@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotNull(message = "Order type cannot be null")
    @Schema(description = "Type of the order", requiredMode = Schema.RequiredMode.REQUIRED)
    private OrderType orderType;

    @NotEmpty(message = "Order items list cannot be empty")
    @Valid
    @Schema(description = "List of items in the order", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CreateOrderItemDto> orderItems;
}