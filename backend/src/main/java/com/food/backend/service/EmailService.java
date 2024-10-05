package com.food.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired // This means to get the bean called emailSender
    private JavaMailSender emailSender;

    public void sendTestEmail(String to, String subject, String text)
    throws MessagingException
    {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true indicates multipart message
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true); // true indicates html
        emailSender.send(message);
    }
}
