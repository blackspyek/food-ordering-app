package com.food.backend.service.interfaces;

import com.food.backend.dto.orderdtos.CreateOrderDto;
import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.model.Order;
import com.food.backend.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IOrderService {
    Order createOrder(CreateOrderDto createOrderDto);
    Optional<Order> getOrderById(UUID orderId);
    OrderStatus getOrderStatus(UUID orderId);
    Order updateOrderStatus(UUID orderId, OrderStatus newStatus, String username);
    void deleteOrder(UUID orderId);
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOrdersByPreparedBy(User preparedBy);
    List<Order> getAllOrdersWithItems();
    Order updateOrderPreparedBy(UUID orderId, String userName);
    String forceAddOrderNumberToTheBoard();
    List<Order> get10LastOrdersByUserId(Long userId);
}
