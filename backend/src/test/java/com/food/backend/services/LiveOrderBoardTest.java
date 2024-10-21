package com.food.backend.services;

import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.service.LiveOrderBoard;
import com.food.backend.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LiveOrderBoardTest {

    @Mock
    private OrderService orderService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private LiveOrderBoard liveOrderBoard;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Simulate behavior of OrderService
        when(orderService.getOrdersByStatus(OrderStatus.IN_PREPARATION)).thenReturn(List.of());
        when(orderService.getOrdersByStatus(OrderStatus.READY_FOR_PICKUP)).thenReturn(List.of());
    }

    @Test
    void testGenerateOrderBoardCode() {
        String orderCode = liveOrderBoard.generateOrderBoardCode();
        assertNotNull(orderCode);
        assertTrue(liveOrderBoard.getLiveOrderBoardCodes().contains(orderCode));
    }

    @Test
    void testMoveOrderCodeToReady() {
        String orderCode = liveOrderBoard.generateOrderBoardCode();
        liveOrderBoard.moveOrderCodeToReady(orderCode);
        assertFalse(liveOrderBoard.getLiveOrderBoardCodes().contains(orderCode));
        assertTrue(liveOrderBoard.getReadyOrderBoardCodes().contains(orderCode));
    }

    @Test
    void testRemoveOrderCode() {
        String orderCode = liveOrderBoard.generateOrderBoardCode();
        liveOrderBoard.removeOrderCode(orderCode);
        assertFalse(liveOrderBoard.getLiveOrderBoardCodes().contains(orderCode));
        assertFalse(liveOrderBoard.getReadyOrderBoardCodes().contains(orderCode));
    }

    @Test
    void testSendUpdatedOrderBoard() {
        // Reset the mock to clear any interactions
        reset(messagingTemplate);

        // Act
        liveOrderBoard.sendUpdatedOrderBoard();

        // Assert
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/orderBoard"), anyMap());
    }
}
