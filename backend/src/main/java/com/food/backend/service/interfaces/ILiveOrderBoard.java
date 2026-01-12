package com.food.backend.service.interfaces;

import com.food.backend.model.Order;

import java.util.Map;
import java.util.Set;

public interface ILiveOrderBoard {
    String generateOrderBoardCode();
    void moveOrderCodeToReady(String orderCode);
    void moveOrderCodetoLive(String orderCode);
    void removeOrderCode(String orderCode);
    void sendUpdatedOrderBoard();
    void sendKitchenOrderUpdate(Order order, String action);
    Map<String, Set<String>> getOrderBoardState();
}
