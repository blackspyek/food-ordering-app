package com.food.backend.dto.orderdtos;

import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.model.Enums.OrderType;
import com.food.backend.model.Order;
import com.food.backend.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KitchenOrderUpdateDto {
    private String action; // "NEW", "STATUS_CHANGED", "REMOVED"
    private KitchenOrderDto order;

    public static KitchenOrderUpdateDto fromOrder(Order order, String action) {
        KitchenOrderUpdateDto dto = new KitchenOrderUpdateDto();
        dto.setAction(action);
        dto.setOrder(KitchenOrderDto.fromOrder(order));
        return dto;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KitchenOrderDto {
        private UUID orderId;
        private String boardCode;
        private OrderStatus status;
        private OrderType orderType;
        private String name;
        private String email;
        private Double totalPrice;
        private LocalDateTime orderTime;
        private List<KitchenOrderItemDto> orderItems;

        public static KitchenOrderDto fromOrder(Order order) {
            KitchenOrderDto dto = new KitchenOrderDto();
            dto.setOrderId(order.getOrderId());
            dto.setBoardCode(order.getBoardCode());
            dto.setStatus(order.getStatus());
            dto.setOrderType(order.getOrderType());
            dto.setName(order.getName());
            dto.setEmail(order.getEmail());
            dto.setTotalPrice(order.getTotalPrice());
            dto.setOrderTime(order.getOrderTime());
            if (order.getOrderItems() != null) {
                dto.setOrderItems(order.getOrderItems().stream()
                        .map(KitchenOrderItemDto::fromOrderItem)
                        .collect(Collectors.toList()));
            }
            return dto;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KitchenOrderItemDto {
        private Long orderItemId;
        private String itemName;
        private Integer quantity;
        private Double totalPrice;

        public static KitchenOrderItemDto fromOrderItem(OrderItem orderItem) {
            KitchenOrderItemDto dto = new KitchenOrderItemDto();
            dto.setOrderItemId(orderItem.getOrderItemId());
            dto.setItemName(orderItem.getItem() != null ? orderItem.getItem().getName() : "Unknown");
            dto.setQuantity(orderItem.getQuantity());
            dto.setTotalPrice(orderItem.getTotalPrice());
            return dto;
        }
    }
}
