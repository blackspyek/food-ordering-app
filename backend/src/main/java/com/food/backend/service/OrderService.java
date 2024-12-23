package com.food.backend.service;

import com.food.backend.dto.orderdtos.CreateOrderDto;
import com.food.backend.dto.orderdtos.CreateOrderItemDto;
import com.food.backend.dto.orderdtos.OrderDto;
import com.food.backend.dto.orderdtos.OrderItemListingDto;
import com.food.backend.model.Order;
import com.food.backend.model.OrderItem;
import com.food.backend.model.MenuItem;
import com.food.backend.model.User;
import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.model.Enums.OrderType;
import com.food.backend.repository.OrderRepository;
import com.food.backend.repository.OrderItemRepository;
import com.food.backend.exception.OrderNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemService menuItemService;
    private final LiveOrderBoard liveOrderBoard;
    private final EmailService emailService;
    private final UserService userService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, MenuItemService menuItemService, @Lazy LiveOrderBoard liveOrderBoard, EmailService emailService, UserService userService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.menuItemService = menuItemService;
        this.liveOrderBoard = liveOrderBoard;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Transactional
    public Order createOrder(CreateOrderDto createOrderDto)  {
        Order order = initializeOrder(createOrderDto.getOrderType(), createOrderDto.getEmail());
        Order savedOrder = orderRepository.save(order);
        List<OrderItem> orderItemsList = createOrderItemsList(savedOrder, createOrderDto.getOrderItems());
        calculateAndSetTotalPrice(order, orderItemsList);
        saveOrder(order, orderItemsList);
        savedOrder.setOrderItems(orderItemsList);
        return savedOrder;
    }

    private void saveOrder(Order order, List<OrderItem> orderItemsList) {
        try {
            order.setBoardCode(liveOrderBoard.generateOrderBoardCode());
            orderRepository.save(order);
            saveOrderItems(order, orderItemsList);
        } catch (Exception e) {
            liveOrderBoard.removeOrderCode(order.getBoardCode());
            throw new RuntimeException("Failed to create order", e);
        }
        finally {
            sendOrderConfirmationEmail(order.getEmail(), OrderDto.fromOrder(order, getOrderItemsListingDto(orderItemsList)));
        }
    }

    private List<OrderItem> createOrderItemsList(Order order, List<CreateOrderItemDto> orderItems) {
        return orderItems.stream()
                .map(itemDto -> {
                    MenuItem menuItem = menuItemService.findById(itemDto.getMenuItemId())
                            .orElseThrow(() -> new EntityNotFoundException("Menu item with id " + itemDto.getMenuItemId() + " not found"));
                    return createOrderItem(order, menuItem, itemDto.getQuantity());
                })
                .collect(Collectors.toList());
    }

    private List<OrderItemListingDto> getOrderItemsListingDto(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItemListingDto::fromOrderItem)
                .collect(Collectors.toList());
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
    public OrderStatus getOrderStatus(Long orderId) {
        return orderRepository.findStatusById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }


    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) throws IllegalArgumentException {
        checkIfStatusIsValid(newStatus.toString());

        Order order = findOrderOrThrow(orderId);
        order.setStatus(newStatus);
        moveOrderNumberOnTheBoardBasedOnStatus(newStatus, order);
        return orderRepository.save(order);
    }

    private void moveOrderNumberOnTheBoardBasedOnStatus(OrderStatus newStatus, Order order) {
        if (newStatus == OrderStatus.READY_FOR_PICKUP) {
            liveOrderBoard.moveOrderCodeToReady(order.getBoardCode());
        } else if (newStatus == OrderStatus.IN_PREPARATION) {
            liveOrderBoard.moveOrderCodetoLive(order.getBoardCode());
        }
        else {
            liveOrderBoard.removeOrderCode(order.getBoardCode());
        }
    }

    private static void checkIfStatusIsValid(String orderStatus) throws IllegalArgumentException {
        OrderStatus.valueOf(orderStatus);
    }

    private static void checkIfTypeIsValid(String orderType) throws IllegalArgumentException {
        OrderType.valueOf(orderType);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        liveOrderBoard.removeOrderCode(order.getBoardCode());
        orderRepository.deleteById(orderId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.getOrdersByStatus(status);
    }

    public List<Order> getOrdersByPreparedBy(User preparedBy) {
        return orderRepository.getOrdersByPreparedBy(preparedBy);
    }

    public List<Order> getAllOrdersWithItems() {
        return orderRepository.findAllWithItemsOrderByOrderId();
    }


    private Order initializeOrder(OrderType orderType, String email) throws IllegalArgumentException {
        checkIfTypeIsValid(orderType.toString());
        Order order = new Order();
        order.setEmail(email);
        order.setOrderType(orderType);
        order.setStatus(OrderStatus.IN_PREPARATION);
        order.setOrderTime(LocalDateTime.now());
        return order;
    }


    private double calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }

    private void saveOrderItems(Order order, List<OrderItem> orderItems) {
        orderItems.forEach(item -> {
            item.setOrder(order);
            orderItemRepository.save(item);
        });
    }

    private Order findOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }



    private OrderItem createOrderItem(Order order, MenuItem menuItem, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(menuItem);
        orderItem.setQuantity(quantity);
        orderItem.setTotalPrice(menuItem.getPrice() * quantity);
        return orderItem;
    }


    private void calculateAndSetTotalPrice(Order order, List<OrderItem> orderItems) {
        double totalPrice = calculateTotalPrice(orderItems);
        order.setTotalPrice(totalPrice);
    }

    private void sendOrderConfirmationEmail(String to, OrderDto order) {
        try{
            emailService.sendOrderConfirmationEmail(to, order);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send order confirmation email", e);
        }
    }

    public Order updateOrderPreparedBy(Long orderId, String userName) {
        validateParameters(orderId, userName);
        Order order = findOrderOrThrow(orderId);
        User user = userService.findUserByUsername(userName);

        order.setPreparedBy(user);
        return orderRepository.save(order);
    }
    private void validateParameters(Long orderId, String userName) {
        if (orderId == null || userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("Invalid orderId or userName");
        }
    }

    public String forceAddOrderNumberToTheBoard() {
        return liveOrderBoard.generateOrderBoardCode();
    }
}
