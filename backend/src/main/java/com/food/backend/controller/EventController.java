package com.food.backend.controller;

import com.food.backend.service.LiveOrderBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class EventController {
    private final LiveOrderBoard liveOrderBoard;

    @Autowired
    public EventController(LiveOrderBoard liveOrderBoard) {
        this.liveOrderBoard = liveOrderBoard;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/orderBoard")
    public String handleMessage() {
        return liveOrderBoard.getOrderBoardState().toString();
    }
}
