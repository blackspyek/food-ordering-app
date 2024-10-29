package com.food.backend.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public String sendNotificationToTopic(String topic, String title, String body, String image) {
        Message message = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .setImage(image)
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            return "Notification sent: " + response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send notification";
        }
    }
}
