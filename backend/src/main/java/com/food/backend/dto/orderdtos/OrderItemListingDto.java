package com.food.backend.dto.orderdtos;


import com.food.backend.model.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for listing order items")
public class OrderItemListingDto {

    @NotBlank(message = "MenuItemId cannot be empty")
    @Schema(description = "Name of the menu item", example = "Margherita Pizza", requiredMode = Schema.RequiredMode.REQUIRED)
    private String menuItemName;

    @Schema(description = "Quantity of the item", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive
    private Integer quantity;

    @Schema(description = "Price per item", example = "12.99", requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive
    private Double price;

    @Schema(description = "Total price for this item", example = "25.98", requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive
    private Double totalPrice;

    public static OrderItemListingDto fromOrderItem(OrderItem orderItem) {
        OrderItemListingDto orderItemListingDto = new OrderItemListingDto();
        orderItemListingDto.setMenuItemName(orderItem.getItem().getName());
        orderItemListingDto.setQuantity(orderItem.getQuantity());
        orderItemListingDto.setPrice(orderItem.getItem().getPrice());
        orderItemListingDto.setTotalPrice(orderItem.getTotalPrice());
        return orderItemListingDto;
    }
}