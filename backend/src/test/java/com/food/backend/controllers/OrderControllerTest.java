package com.food.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.backend.dto.orderdtos.CreateOrderDto;
import com.food.backend.dto.orderdtos.CreateOrderItemDto;
import com.food.backend.dto.orderdtos.UpdateOrderStatusDto;
import com.food.backend.dto.orderdtos.UpdatePreparedByDto;
import com.food.backend.exception.OrderNotFoundException;
import com.food.backend.model.Enums.OrderType;
import com.food.backend.model.Order;
import com.food.backend.model.User;
import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.service.OrderService;
import com.food.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder;
    private User testUser;
    private CreateOrderDto createOrderDto;
    private static final String BASE_URL = "/api/orders";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testOrder = new Order();
        testOrder.setOrderId(1L);
        testOrder.setOrderType(OrderType.DINE_IN);
        testOrder.setStatus(OrderStatus.IN_PREPARATION);
        testOrder.setOrderTime(LocalDateTime.now());

        createOrderDto = new CreateOrderDto();
        createOrderDto.setOrderType(OrderType.DINE_IN);
        CreateOrderItemDto createOrderItemDto = new CreateOrderItemDto();
        createOrderItemDto.setMenuItemId(1L);
        createOrderItemDto.setQuantity(1);

        createOrderDto.setOrderItems(Collections.singletonList(createOrderItemDto));
        createOrderDto.setEmail("test@o2.pl");
    }

    @Test
    @WithMockUser
    void createOrder_Success() throws Exception {
        when(orderService.createOrder(any(CreateOrderDto.class))).thenReturn(testOrder);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order created successfully"))
                .andExpect(jsonPath("$.data.orderId").value(1L));

        verify(orderService).createOrder(any(CreateOrderDto.class));
    }

    @Test
    @WithMockUser
    void createOrder_Failure() throws Exception {
        when(orderService.createOrder(any(CreateOrderDto.class)))
                .thenThrow(new RuntimeException("Invalid order"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error creating order: Invalid order"));
    }

    @Test
    @WithMockUser
    void getOrderById_Success() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));

        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order retrieved successfully"))
                .andExpect(jsonPath("$.data.orderId").value(1L));

        verify(orderService).getOrderById(1L);
    }

    @Test
    @WithMockUser
    void getOrderById_NotFound() throws Exception {
        when(orderService.getOrderById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get(BASE_URL + "/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getAllOrders_Success() throws Exception {
        when(orderService.getAllOrdersWithItems()).thenReturn(Collections.singletonList(testOrder));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Orders retrieved successfully"))
                .andExpect(jsonPath("$.data[0].orderId").value(1L));

        verify(orderService).getAllOrdersWithItems();
    }

    @Test
    @WithMockUser
    void getOrderStatus_Success() throws Exception {
        when(orderService.getOrderStatus(1L)).thenReturn(OrderStatus.IN_PREPARATION);

        mockMvc.perform(get(BASE_URL + "/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order status retrieved successfully"))
                .andExpect(jsonPath("$.data").value("IN_PREPARATION"));
    }

    @Test
    @WithMockUser
    void updateOrderStatus_Success() throws Exception {
        UpdateOrderStatusDto statusDto = new UpdateOrderStatusDto();
        statusDto.setOrderStatus(OrderStatus.PICKED_UP);

        when(orderService.updateOrderStatus(eq(1L), any(OrderStatus.class))).thenReturn(testOrder);

        mockMvc.perform(put(BASE_URL + "/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order status updated successfully"));
    }

    @Test
    @WithMockUser
    void deleteOrder_Success() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order deleted successfully"));

        verify(orderService).deleteOrder(1L);
    }

    @Test
    @WithMockUser
    void getOrdersByStatus_Success() throws Exception {
        when(orderService.getOrdersByStatus(OrderStatus.IN_PREPARATION))
                .thenReturn(Collections.singletonList(testOrder));

        mockMvc.perform(get(BASE_URL + "/status/IN_PREPARATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Orders retrieved successfully"))
                .andExpect(jsonPath("$.data[0].orderId").value(1L));
    }

    @Test
    @WithMockUser
    void getOrdersByPreparedBy_Success() throws Exception {
        when(userService.findUserById(1L)).thenReturn(Optional.of(testUser));
        when(orderService.getOrdersByPreparedBy(testUser))
                .thenReturn(Collections.singletonList(testOrder));

        mockMvc.perform(get(BASE_URL + "/preparedBy/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Orders retrieved successfully"))
                .andExpect(jsonPath("$.data[0].orderId").value(1L));
    }

    @Test
    @WithMockUser
    void updateOrderPreparedBy_Success() throws Exception {
        // Given
        UpdatePreparedByDto dto = new UpdatePreparedByDto();
        dto.setUserName("testUser");



        // Mock the service call
        when(orderService.updateOrderPreparedBy(eq(1L), eq("testUser")))
                .thenReturn(testOrder);

        // When & Then
        mockMvc.perform(patch(BASE_URL + "/1/preparedBy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order preparedBy updated successfully"));
    }

    @Test
    @WithMockUser
    void updateOrderPreparedBy_OrderNotFound() throws Exception {
        UpdatePreparedByDto dto = new UpdatePreparedByDto();
        dto.setUserName("testUser");

        when(orderService.updateOrderPreparedBy(eq(999L), eq("testUser")))
                .thenThrow(new OrderNotFoundException(999L));

        mockMvc.perform(patch(BASE_URL + "/999/preparedBy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateOrderStatus_ValidationError() throws Exception {
        UpdateOrderStatusDto statusDto = new UpdateOrderStatusDto();
        // Don't set the status to trigger validation error

        mockMvc.perform(put(BASE_URL + "/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createOrder_ValidationError() throws Exception {
        CreateOrderDto invalidDto = new CreateOrderDto();
        // Leave required fields empty to trigger validation

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
