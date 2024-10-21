package com.food.backend.controllers;

import com.food.backend.controller.EventController;
import com.food.backend.service.LiveOrderBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
class EventControllerTest {

    @Mock
    private LiveOrderBoard liveOrderBoard;


    private EventController eventController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventController = new EventController(liveOrderBoard);
    }

    @Test
    void handleMessage_ShouldReturnOrderBoardState() {
        // Arrange
        Map<String, Set<String>> expectedOrderBoardState = new HashMap<>();
        expectedOrderBoardState.put("liveOrderBoardCodes", new TreeSet<>());
        expectedOrderBoardState.put("liveOrderBoardReadyCodes", new HashSet<>());

        when(liveOrderBoard.getOrderBoardState()).thenReturn(expectedOrderBoardState);

        // Act
        String result = eventController.handleMessage();

        // Assert
        assertNotNull(result);
        assertEquals(expectedOrderBoardState.toString(), result);
        verify(liveOrderBoard, times(1)).getOrderBoardState();
    }

    @Test
    void constructor_ShouldInitializeWithLiveOrderBoard() {
        // Arrange & Act
        EventController controller = new EventController(liveOrderBoard);

        // Assert
        assertNotNull(controller);
    }

    @Test
    void handleMessage_ShouldHandleEmptyOrderBoard() {
        // Arrange
        Map<String, Set<String>> emptyOrderBoardState = new HashMap<>();
        emptyOrderBoardState.put("liveOrderBoardCodes", new TreeSet<>());
        emptyOrderBoardState.put("liveOrderBoardReadyCodes", new HashSet<>());

        when(liveOrderBoard.getOrderBoardState()).thenReturn(emptyOrderBoardState);

        // Act
        String result = eventController.handleMessage();

        // Assert
        assertNotNull(result);
        assertEquals(emptyOrderBoardState.toString(), result);
    }

    @Test
    void handleMessage_ShouldHandleOrderBoardWithData() {
        // Arrange
        Map<String, Set<String>> orderBoardState = new HashMap<>();
        TreeSet<String> liveOrders = new TreeSet<>();
        liveOrders.add("001");
        liveOrders.add("002");

        HashSet<String> readyOrders = new HashSet<>();
        readyOrders.add("003");

        orderBoardState.put("liveOrderBoardCodes", liveOrders);
        orderBoardState.put("liveOrderBoardReadyCodes", readyOrders);

        when(liveOrderBoard.getOrderBoardState()).thenReturn(orderBoardState);

        // Act
        String result = eventController.handleMessage();

        // Assert
        assertNotNull(result);
        assertEquals(orderBoardState.toString(), result);
        assertTrue(result.contains("001"));
        assertTrue(result.contains("002"));
        assertTrue(result.contains("003"));
    }

    @Test
    void handleMessage_ShouldHandleExceptionalCase() {
        // Arrange
        when(liveOrderBoard.getOrderBoardState()).thenThrow(new RuntimeException("Simulated error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> eventController.handleMessage());
        verify(liveOrderBoard, times(1)).getOrderBoardState();
    }
}