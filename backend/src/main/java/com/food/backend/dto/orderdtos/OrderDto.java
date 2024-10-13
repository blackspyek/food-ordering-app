package com.food.backend.dto.orderdtos;

import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.model.Enums.OrderType;
import com.food.backend.model.Order;
import com.food.backend.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class OrderDto {
    @NotNull(message = "orderId cannot be empty")
    private Long orderId;

    @NotNull(message = "boardCode cannot be empty")
    private String boardCode;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be empty")
    private String email;

    private Long preparedById;

    @NotNull(message = "OrderStatus cannot be empty")
    private OrderStatus status;

    @NotNull(message = "OrderType cannot be empty")
    private OrderType orderType;

    @Positive(message = "TotalPrice must be greater than 0")
    private Double totalPrice;

    private LocalDateTime orderTime;

    @Valid
    private List<OrderItemListingDto> orderItems;

    public static OrderDto fromOrder(Order order, List<OrderItemListingDto> orderItems) {
        Long employeeId = Optional.ofNullable(order.getPreparedBy())
                .map(User::getId)
                .orElse(null);
        return new OrderDto(
                order.getOrderId(),
                order.getBoardCode(),
                order.getEmail(),
                employeeId,
                order.getStatus(),
                order.getOrderType(),
                order.getTotalPrice(),
                order.getOrderTime(),
                orderItems
        );
    }

    public String formatBoardCode() {
        int boardCodeNumber = Integer.parseInt(boardCode);
        return String.format("%02d", boardCodeNumber);
    }
}