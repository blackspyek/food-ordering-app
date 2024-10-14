package com.food.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class EventController {
    @MessageMapping("/event")
    @SendTo("/topic/event")
    public String handleEvent(
            @Payload String message,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String sessiosnId = headerAccessor.getSessionId();
        System.out.println("Session ID: " + sessiosnId);
        return "Received: " + message;
    }
}
