package com.food.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "order_items")
@Schema(description = "Entity representing an item in an order")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the order item", example = "1")
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    @Schema(description = "The order this item belongs to")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @Schema(description = "The menu item ordered")
    private MenuItem item;

    @Column(nullable = false)
    @Schema(description = "Quantity of the item ordered", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @Column(nullable = false)
    @Schema(description = "Total price for this order item", example = "25.98", requiredMode = Schema.RequiredMode.REQUIRED)
    private double totalPrice;

}
