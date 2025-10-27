package com.food.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.model.Enums.OrderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
@Schema(description = "Entity representing an order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "Unique identifier of the order", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
    private UUID orderId;

    @ManyToOne
    @JoinColumn(name = "prepared_by_user_id", nullable = true)
    @Schema(description = "Employee who prepared the order")
    private User preparedBy;

    @ManyToOne
    @JoinColumn(name = "ordered_by_user_id", nullable = true)
    @Schema(description = "Customer who placed the order")
    private User orderedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Current status of the order", requiredMode = Schema.RequiredMode.REQUIRED)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    @Schema(description = "Type of the order", requiredMode = Schema.RequiredMode.REQUIRED)
    private OrderType orderType;

    @Column(name = "total_price", nullable = true)
    @Schema(description = "Total price of the order", example = "25.99")
    private Double totalPrice;

    @Column(name = "order_time", nullable = false, updatable = false)
    @Schema(description = "Time when the order was placed", example = "2023-06-15T14:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime orderTime;

    @Column(name = "board_code", nullable = true)
    @Schema(description = "Code for the board/table", example = "05")
    private String boardCode;

    @Column(name = "email", nullable = false)
    @Schema(description = "Email of the customer", example = "customer@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Column(name = "name", nullable = false)
    @Schema(description = "Name of the customer", example = "Joan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "List of items in the order")
    @JsonManagedReference
    private List<OrderItem> orderItems;

}
