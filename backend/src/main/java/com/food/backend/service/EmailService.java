package com.food.backend.service;

import com.food.backend.dto.orderdtos.OrderDto;
import com.food.backend.dto.orderdtos.OrderItemListingDto;
import com.food.backend.service.interfaces.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {
    private final JavaMailSender emailSender;

    public void sendOrderConfirmationEmail(String recipient, OrderDto order) {
        try {
            MimeMessage message = createEmailMessage(recipient, order);
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send order confirmation email", e);
        }
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
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        String formattedTime = order.getOrderTime().format(formatter);

        return String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<meta charset=\"UTF-8\">" +
                        "<style>" +
                        "   body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; -webkit-font-smoothing: antialiased; }" +
                        "   .wrapper { width: 100%%; background-color: #f4f4f4; padding: 20px 0; }" +
                        "   .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }" +
                        "   .header { background-color: #333333; padding: 20px; text-align: center; }" +
                        "   .header h1 { color: #ffffff; margin: 0; font-size: 24px; font-weight: 600; letter-spacing: 1px; }" +
                        "   .content { padding: 30px; }" +
                        "   .intro-text { color: #555555; font-size: 16px; line-height: 1.6; margin-bottom: 20px; text-align: center; }" +
                        "   .order-meta { text-align: center; color: #888888; font-size: 14px; margin-bottom: 30px; }" +

                        "   .board-wrapper { text-align: center; margin: 30px 0; }" +
                        "   .board-table { margin: 0 auto; border-spacing: 0; border-collapse: separate; border-radius: 12px; overflow: hidden; border: 1px solid #000000; }" +
                        "   .board-num-cell { background-color: #fda403; color: #1a1a1a; font-size: 64px; font-weight: bold; padding: 10px 25px; vertical-align: middle; line-height: 1; border-right: 1px solid #fda403; }" +
                        "   .board-text-cell { background-color: #000000; color: #fda403; font-size: 14px; font-weight: 600; text-transform: uppercase; letter-spacing: 1px; padding: 0 20px; vertical-align: middle; text-align: left; }" +

                        "   .btn-wrapper { text-align: center; margin-top: 30px; margin-bottom: 10px; }" +
                        "   .track-btn { background-color: #fda403; color: #1a1a1a; padding: 14px 30px; border-radius: 50px; text-decoration: none; font-weight: bold; display: inline-block; font-size: 16px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }" +

                        "   .items-table { width: 100%%; border-collapse: collapse; margin-top: 20px; }" +
                        "   .items-table th { padding: 12px; background-color: #f8f8f8; color: #555; font-size: 12px; text-transform: uppercase; border-bottom: 2px solid #eaeaea; }" +
                        "   .items-table td { padding: 12px; border-bottom: 1px solid #eaeaea; color: #333; font-size: 14px; }" +

                        "   .items-table th:nth-child(1), .items-table td:nth-child(1) { text-align: left; }" +
                        "   .items-table th:nth-child(2), .items-table td:nth-child(2) { text-align: center; }" +
                        "   .items-table th:nth-child(3), .items-table td:nth-child(3) { text-align: right; }" +
                        "   .items-table th:nth-child(4), .items-table td:nth-child(4) { text-align: right; }" +

                        "   .items-table tr:last-child td { border-bottom: none; }" +

                        "   .footer { background-color: #f9f9f9; padding: 20px; text-align: center; font-size: 12px; color: #999999; border-top: 1px solid #eaeaea; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class=\"wrapper\">" +
                        "   <div class=\"container\">" +
                        "       <div class=\"header\">" +
                        "           <h1>Witaj " + order.getName() + "!</h1>" +
                        "       </div>" +
                        "       <div class=\"content\">" +
                        "           <p class=\"intro-text\">Thank you for your order! We are preparing your meal.</p>" +
                        "           <div class=\"order-meta\">" +
                        "               <strong>Order ID:</strong> #%s &nbsp;|&nbsp; <strong>Time:</strong> %s" +
                        "           </div>" +

                        "           <div class=\"board-wrapper\">" +
                        "               <table class=\"board-table\">" +
                        "                   <tr>" +
                        "                       <td class=\"board-num-cell\">%s</td>" +
                        "                       <td class=\"board-text-cell\">Board<br>Number</td>" +
                        "                   </tr>" +
                        "               </table>" +
                        "           </div>" +

                        "           <table class=\"items-table\">" +
                        "               <thead>" +
                        "                   <tr>" +
                        "                       <th>Item</th>" +
                        "                       <th>Qty</th>" +
                        "                       <th>Price</th>" +
                        "                       <th>Total</th>" +
                        "                   </tr>" +
                        "               </thead>" +
                        "               <tbody>" +
                        getHtmlOfItems(order.getOrderItems()) +
                        "               </tbody>" +
                        "           </table>" +

                        "           <div class=\"btn-wrapper\">" +
                        "               <a href=\"http://localhost:4200/order-details/%s\" class=\"track-btn\">Track Your Order</a>" +
                        "           </div>" +

                        "       </div>" +
                        "       <div class=\"footer\">" +
                        "           &copy; %d SLURP. All rights reserved.<br>" +
                        "           Enjoy your meal!" +
                        "       </div>" +
                        "   </div>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                order.getOrderId(),
                formattedTime,
                order.formatBoardCode(),
                order.getOrderId(),
                Calendar.getInstance().get(Calendar.YEAR)
        );
    }
    private String getHtmlOfItems(List<OrderItemListingDto> orderItems) {
        StringBuilder html = new StringBuilder();
        for (OrderItemListingDto item : orderItems) {
            html.append(String.format(
                    "<tr>" +
                            "<td style=\"text-align:center;\">%s</td>" +
                            "<td style=\"text-align:center;\">%s</td>" +
                            "<td style=\"text-align:center;\">%s</td>" +
                            "<td style=\"text-align:center;\">%s</td>" +
                            "</tr>",
                    item.getMenuItemName(),
                    item.getQuantity(),
                    item.getPrice(),
                    item.getQuantity() * item.getPrice()
            ));
        }
        return html.toString();
    }

    public void sendPasswordResetEmail(String recipient, String resetLink) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(recipient);
            helper.setSubject("SLURP - Password Reset Request");
            helper.setText(getPasswordResetEmailBody(resetLink), true);

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String getPasswordResetEmailBody(String resetLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<style>" +
                "   body { font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; -webkit-font-smoothing: antialiased; }" +
                "   .wrapper { width: 100%; background-color: #f4f4f4; padding: 20px 0; }" +
                "   .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }" +
                "   .header { background-color: #333333; padding: 20px; text-align: center; }" +
                "   .header h1 { color: #ffffff; margin: 0; font-size: 24px; font-weight: 600; letter-spacing: 1px; }" +
                "   .content { padding: 30px; text-align: center; }" +
                "   .intro-text { color: #555555; font-size: 16px; line-height: 1.6; margin-bottom: 30px; }" +
                "   .btn-wrapper { text-align: center; margin: 30px 0; }" +
                "   .reset-btn { background-color: #fda403; color: #1a1a1a; padding: 14px 30px; border-radius: 50px; text-decoration: none; font-weight: bold; display: inline-block; font-size: 16px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }" +
                "   .expire-text { color: #888888; font-size: 14px; margin-top: 20px; }" +
                "   .footer { background-color: #f9f9f9; padding: 20px; text-align: center; font-size: 12px; color: #999999; border-top: 1px solid #eaeaea; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"wrapper\">" +
                "   <div class=\"container\">" +
                "       <div class=\"header\">" +
                "           <h1>Password Reset</h1>" +
                "       </div>" +
                "       <div class=\"content\">" +
                "           <p class=\"intro-text\">We received a request to reset your password. Click the button below to create a new password.</p>" +
                "           <div class=\"btn-wrapper\">" +
                "               <a href=\"" + resetLink + "\" class=\"reset-btn\">Reset Password</a>" +
                "           </div>" +
                "           <p class=\"expire-text\">This link will expire in 1 hour. If you didn't request this, please ignore this email.</p>" +
                "       </div>" +
                "       <div class=\"footer\">" +
                "           <p>&copy; " + Calendar.getInstance().get(Calendar.YEAR) + " SLURP. All rights reserved.</p>" +
                "       </div>" +
                "   </div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

}
