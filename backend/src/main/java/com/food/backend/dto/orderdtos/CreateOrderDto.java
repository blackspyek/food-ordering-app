package com.food.backend.dto.orderdtos;

import com.food.backend.model.Enums.OrderType;
import com.food.backend.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Name cannot be empty")
    @Schema(description = "Name of the customer", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "Order type cannot be null")
    @Schema(description = "Type of the order", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    private OrderType orderType;

    @NotEmpty(message = "Order items list cannot be empty")
    @Valid
    @Schema(description = "List of items in the order", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CreateOrderItemDto> orderItems;

    @Schema(description = "User ID of the customer placing the order", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
    private Long userId;
}