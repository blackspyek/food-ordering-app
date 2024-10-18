package com.food.backend.controller;

import com.food.backend.dto.orderdtos.CreateOrderDto;
import com.food.backend.dto.orderdtos.UpdateOrderStatusDto;
import com.food.backend.model.Order;
import com.food.backend.model.User;
import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.service.OrderService;
import com.food.backend.exception.OrderNotFoundException;
import com.food.backend.service.UserService;
import com.food.backend.utils.classes.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @Valid
            @RequestBody
            CreateOrderDto createOrderDTO) {
        try {
            Order order = orderService.createOrder(createOrderDTO);
            return ResponseUtil.successResponse(order, "Order created successfully");
        } catch (Exception e) {
            return ResponseUtil.badRequestResponse("Error creating order: " + e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new OrderNotFoundException(orderId));
            return ResponseUtil.successResponse(order, "Order retrieved successfully");
        } catch (OrderNotFoundException e) {
            return ResponseUtil.notFoundResponse(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseUtil.successResponse(orders, "Orders retrieved successfully");
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                               @RequestBody
                                               @Valid
                                               UpdateOrderStatusDto updateOrderStatusDto) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, updateOrderStatusDto.getOrderStatus());
            return ResponseUtil.successResponse(updatedOrder, "Order status updated successfully");
        } catch (OrderNotFoundException e) {
            return ResponseUtil.notFoundResponse(e.getMessage());
        }
        catch (Exception e) {
            return ResponseUtil.badRequestResponse("Error updating order status: " + e.getMessage());
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseUtil.successResponse(null, "Order deleted successfully");
        } catch (OrderNotFoundException e) {
            return ResponseUtil.notFoundResponse(e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseUtil.successResponse(orders, "Orders retrieved successfully");
    }

    @GetMapping("/preparedBy/{userId}")
    public ResponseEntity<?> getOrdersByPreparedBy(@PathVariable Long userId) {
        User preparedBy = userService.findUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + userId + " not found"));
        List<Order> orders = orderService.getOrdersByPreparedBy(preparedBy);
        return ResponseUtil.successResponse(orders, "Orders retrieved successfully");
    }


}
