package com.food.backend.service;

import com.food.backend.dto.orderdtos.OrderDto;
import com.food.backend.dto.orderdtos.OrderItemListingDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {
    private final JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }


    public void sendOrderConfirmationEmail(String recipient, OrderDto order) throws MessagingException {
        MimeMessage message = createEmailMessage(recipient, order);
        emailSender.send(message);
    }

    private MimeMessage createEmailMessage(String recipient, OrderDto order) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true indicates multipart message

        helper.setTo(recipient);
        helper.setSubject(buildEmailSubject(order));
        helper.setText(getOrderConfirmationEmailBody(order), true); // true indicates html

        return message;
    }

    private String buildEmailSubject(OrderDto order) {
        return String.format("SLURP - Order Confirmation: %s", order.getOrderId());
    }


    private String getOrderConfirmationEmailBody(OrderDto order) {
        return String.format(
                "<html>" +
                        "<head>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; margin: 0; padding: 0; }" +
                        ".container { max-width: 600px; margin: auto; padding: 20px; border: 1px solid #eaeaea; border-radius: 5px; background-color: #f9f9f9; }" +
                        "h1 { color: #333; }" +
                        "p { font-size: 16px; color: #555; }" +
                        ".footer { margin-top: 20px; font-size: 12px; color: #888; }" +
                        ".board { display: flex; flex-direction: column; justify-content: center; align-items: center; width: 100%%} " +
                        ".boardNumber { text-align: center; font-size: 72px; background-color: #fda403; padding: 20px; border-radius: 15px 0px 0px 15px; } " +
                        ".boardText { color: #fda403; background-color: black; padding: 10px; border-radius: 0 15px 15px 0px; } " +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class='container'>" +
                        "<h1>Order Confirmation</h1>" +
                        "<p>Thank you for your order!</p>" +
                        "<p><strong>Order ID:</strong>#%s</p>" +
                        "<p><strong>Order Time:</strong> %s</p>" +
                        "<div class=\"board\">" +
                        "<span class=\"boardNumber\">%s</span><span class=\"boardText\">Board number</span>" +
                        "</div>" +
                        "<table>" +
                        "<tr>" +
                        "<th>Item</th>" +
                        "<th>Quantity</th>" +
                        "<th>Price</th>" +
                        "<th>Total</th>" +
                        "</tr>" +
                        getHtmlOfItems(order.getOrderItems()) +
                        "</table>" +
                        "<p>We appreciate your business and hope you enjoy your meal!</p>" +
                        "<div class='footer'>" +
                        "<p>&copy; %d SLURP. All rights reserved.</p>" +
                        "</div>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                order.getOrderId(),
                order.getOrderTime(),
                order.formatBoardCode(),
                java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) // Current year
        );
    }
    private String getHtmlOfItems(List<OrderItemListingDto> orderItems) {
        StringBuilder html = new StringBuilder();
        for (OrderItemListingDto item : orderItems) {
            html.append(String.format(
                    "<tr>" +
                            "<td>%s</td>" +
                            "<td>%s</td>" +
                            "<td>%s</td>" +
                            "<td>%s</td>" +
                            "</tr>",
                    item.getMenuItemName(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getQuantity() * item.getPrice()
            ));
        }
        return html.toString();
    }


}
