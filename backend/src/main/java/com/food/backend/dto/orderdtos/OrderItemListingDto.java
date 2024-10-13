package com.food.backend.dto.orderdtos;


import com.food.backend.model.OrderItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemListingDto {

    @NotBlank(message = "MenuItemId cannot be empty")
    private String menuItemName;

    @Positive
    private Integer quantity;

    @Positive
    private Double price;
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