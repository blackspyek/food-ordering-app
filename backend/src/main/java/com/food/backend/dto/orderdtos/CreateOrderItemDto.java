package com.food.backend.dto.orderdtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for creating an order item")
public class CreateOrderItemDto {
    @NotNull(message = "Menu item id cannot be null")
    @Schema(description = "ID of the menu item", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long menuItemId;

    @Positive(message = "Quantity must be greater than 0")
    @Schema(description = "Quantity of the item", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;
}
