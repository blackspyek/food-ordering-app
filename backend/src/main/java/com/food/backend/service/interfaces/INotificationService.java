package com.food.backend.service.interfaces;

public interface INotificationService {
    String sendNotificationToTopic(String topic, String title, String body, String image);
}
