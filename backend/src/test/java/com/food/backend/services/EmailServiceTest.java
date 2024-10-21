package com.food.backend.services;


import com.food.backend.dto.orderdtos.OrderDto;
import com.food.backend.dto.orderdtos.OrderItemListingDto;
import com.food.backend.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private MimeMessage mimeMessage;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        emailService = new EmailService(emailSender);
    }

    @Test
    void sendOrderConfirmationEmail_SuccessfulSend() throws MessagingException {
        // Arrange
        String recipient = "test@example.com";
        OrderDto order = createSampleOrder();

        // Act
        emailService.sendOrderConfirmationEmail(recipient, order);

        // Assert
        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendOrderConfirmationEmail_VerifyEmailContent() throws MessagingException {
        // Arrange
        String recipient = "test@example.com";
        OrderDto order = createSampleOrder();

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);

        // Act
        emailService.sendOrderConfirmationEmail(recipient, order);

        // Assert
        verify(emailSender).send(mimeMessageCaptor.capture());
        MimeMessage sentMessage = mimeMessageCaptor.getValue();
        assertNotNull(sentMessage);
    }


    @Test
    void sendOrderConfirmationEmail_MultipleItems() throws MessagingException {
        // Arrange
        String recipient = "test@example.com";
        OrderDto order = createMultipleItemOrder();

        // Act
        emailService.sendOrderConfirmationEmail(recipient, order);

        // Assert
        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendOrderConfirmationEmail_EmptyItemList() throws MessagingException {
        // Arrange
        String recipient = "test@example.com";
        OrderDto order = createEmptyOrder();

        // Act
        emailService.sendOrderConfirmationEmail(recipient, order);

        // Assert
        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendOrderConfirmationEmail_NullRecipient() {
        // Arrange
        OrderDto order = createSampleOrder();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                emailService.sendOrderConfirmationEmail(null, order)
        );
    }

    // Helper methods to create test data
    private OrderDto createSampleOrder() {
        OrderDto order = new OrderDto();
        order.setOrderId(123L);
        order.setOrderTime(LocalDateTime.now());
        order.setBoardCode("42");

        OrderItemListingDto item = new OrderItemListingDto();
        item.setMenuItemName("Burger");
        item.setQuantity(1);
        item.setPrice(10.99);

        order.setOrderItems(List.of(item));
        return order;
    }

    private OrderDto createMultipleItemOrder() {
        OrderDto order = new OrderDto();
        order.setOrderId(456L);
        order.setOrderTime(LocalDateTime.now());
        order.setBoardCode("43");

        OrderItemListingDto item1 = new OrderItemListingDto();
        item1.setMenuItemName("Pizza");
        item1.setQuantity(2);
        item1.setPrice(15.99);

        OrderItemListingDto item2 = new OrderItemListingDto();
        item2.setMenuItemName("Salad");
        item2.setQuantity(1);
        item2.setPrice(8.99);

        order.setOrderItems(Arrays.asList(item1, item2));
        return order;
    }

    private OrderDto createEmptyOrder() {
        OrderDto order = new OrderDto();
        order.setOrderId(123L);
        order.setOrderTime(LocalDateTime.now());
        order.setBoardCode("44");
        order.setOrderItems(List.of());
        return order;
    }
}