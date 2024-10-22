package com.food.backend.dto.orderdtos;

import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.model.Enums.OrderType;
import com.food.backend.model.Order;
import com.food.backend.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO for order details")
public class OrderDto {
    @NotNull(message = "orderId cannot be empty")
    @Schema(description = "Unique identifier for the order", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @NotNull(message = "boardCode cannot be empty")
    @Schema(description = "Code for the board/table", example = "05", requiredMode = Schema.RequiredMode.REQUIRED)
    private String boardCode;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be empty")
    @Schema(description = "Email of the customer", example = "customer@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "ID of the employee who prepared the order", example = "101")
    private Long preparedById;

    @NotNull(message = "OrderStatus cannot be empty")
    @Schema(description = "Current status of the order", requiredMode = Schema.RequiredMode.REQUIRED)
    private OrderStatus status;

    @NotNull(message = "OrderType cannot be empty")
    @Schema(description = "Type of the order", requiredMode = Schema.RequiredMode.REQUIRED)
    private OrderType orderType;

    @Positive(message = "TotalPrice must be greater than 0")
    @Schema(description = "Total price of the order", example = "25.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double totalPrice;

    @Schema(description = "Time when the order was placed", example = "2023-06-15T14:30:00")
    private LocalDateTime orderTime;

    @Valid
    @Schema(description = "List of items in the order")
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