package com.food.backend.service.interfaces;

import com.food.backend.dto.orderdtos.OrderDto;

public interface IEmailService {
    void sendOrderConfirmationEmail(String recipient, OrderDto order);
    void sendPasswordResetEmail(String recipient, String resetLink);
}
