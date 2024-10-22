package com.food.backend.exception;

public class OrderItemNotFoundException extends RuntimeException {
    public OrderItemNotFoundException(Long orderItemId) {
        super("Order item with ID " + orderItemId + " not found.");
    }
}