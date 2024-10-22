package com.food.backend.service;

import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.model.Order;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;


@Slf4j
@Getter
@Service
public class LiveOrderBoard {
    private final SortedSet<String> liveOrderBoardCodes;
    private final Set<String> readyOrderBoardCodes;
    private final OrderService orderService;
    private static Integer LAST_NUMBER = -1;

    private final SimpMessagingTemplate messagingTemplate;
    private final Logger logger = Logger.getLogger(LiveOrderBoard.class.getName());

    @Autowired
    public LiveOrderBoard(@Lazy OrderService orderService, SimpMessagingTemplate messagingTemplate) {
        this.orderService = orderService;
        this.messagingTemplate = messagingTemplate;
        this.liveOrderBoardCodes = new TreeSet<>();
        this.readyOrderBoardCodes = new HashSet<>();
        initializeOrderSets();

    }

    private void initializeOrderSets() {
        clearOrderSets();
        loadOrdersIntoSets();
        sendUpdatedOrderBoard();
        getLastOrderNumber();
    }
    private void getLastOrderNumber() {
        Number maxOrderNumber = getMaxOrderNumberFromLiveOrderBoardCodesList(liveOrderBoardCodes.stream());
        Number maxReadyOrderNumber = getMaxOrderNumberFromLiveOrderBoardCodesList(readyOrderBoardCodes.stream());
        LAST_NUMBER = Math.max(maxOrderNumber.intValue(), maxReadyOrderNumber.intValue());
    }

    private Number getMaxOrderNumberFromLiveOrderBoardCodesList(Stream<String> liveOrderBoardCodes) {
        return liveOrderBoardCodes
                .map(Integer::parseInt)
                .max(Integer::compareTo)
                .orElse(-1);
    }

    private void clearOrderSets() {
        liveOrderBoardCodes.clear();
        readyOrderBoardCodes.clear();
    }

    private void loadOrdersIntoSets() {
        addOrdersToSet(orderService.getOrdersByStatus(OrderStatus.IN_PREPARATION), liveOrderBoardCodes);
        addOrdersToSet(orderService.getOrdersByStatus(OrderStatus.READY_FOR_PICKUP), readyOrderBoardCodes);
    }

    private void addOrdersToSet(List<Order> orders, Set<String> targetSet) {
        orders.forEach(order -> targetSet.add(order.getBoardCode()));
    }

    public String generateOrderBoardCode() {
        if (++LAST_NUMBER == 100)  LAST_NUMBER = 0;
        String orderCode = LAST_NUMBER.toString();
        addNewOrderCode(orderCode);
        return orderCode;
    }
    private void addNewOrderCode(String orderCode) {
        liveOrderBoardCodes.add(orderCode);
        sendUpdatedOrderBoard();
    }
    public void moveOrderCodeToReady(String orderCode) {
        try{
            liveOrderBoardCodes.remove(orderCode);
        }
        catch (Exception e) {
            this.logger.info("Order code not found in live order board");
        }
        finally {
            readyOrderBoardCodes.add(orderCode);
            sendUpdatedOrderBoard();
        }

    }
    public void moveOrderCodetoLive(String orderCode) {
        try{
            readyOrderBoardCodes.remove(orderCode);
        }
        catch (Exception e) {
            this.logger.info("Order code not found in ready order board");
        }
        finally {
            liveOrderBoardCodes.add(orderCode);
            sendUpdatedOrderBoard();
        }

    }
    public void removeOrderCode(String orderCode) {
        liveOrderBoardCodes.remove(orderCode);
        readyOrderBoardCodes.remove(orderCode);
        sendUpdatedOrderBoard();
    }
    public void sendUpdatedOrderBoard() {
        this.logger.info("Sending updated order board state");
        Map<String, Set<String>> orderBoardState = getOrderBoardState();
        messagingTemplate.convertAndSend("/topic/orderBoard", orderBoardState);
    }

    public Map<String, Set<String>> getOrderBoardState() {
        return Map.of(
                "liveOrderBoardCodes", liveOrderBoardCodes,
                "liveOrderBoardReadyCodes", readyOrderBoardCodes
        );
    }
}
