package com.food.backend.controller;

import com.food.backend.dto.orderdtos.CreateOrderDto;
import com.food.backend.dto.orderdtos.UpdateOrderStatusDto;
import com.food.backend.dto.orderdtos.UpdatePreparedByDto;
import com.food.backend.model.Order;
import com.food.backend.model.User;
import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.service.OrderService;
import com.food.backend.exception.OrderNotFoundException;
import com.food.backend.service.UserService;
import com.food.backend.utils.classes.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order Management APIs")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @Operation(
            summary = "Create a new order",
            description = "Creates a new order in the system with the provided details"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Order created successfully",
                    content = @Content(schema = @Schema(implementation = Order.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input / Bad request"
            )
    })

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

    @Operation(
            summary = "Get order by ID",
            description = "Retrieves an order by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
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

    @Operation(
            summary = "Get all orders",
            description = "Retrieves all orders in the system including their items"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved all orders",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Order.class)))
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderService.getAllOrdersWithItems();
        return ResponseUtil.successResponse(orders, "Orders retrieved successfully");
    }


    @Operation(
            summary = "Get order status",
            description = "Retrieves the current status of a specific order"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping("/{orderId}/status")
    public ResponseEntity<?> getOrderStatus(@PathVariable Long orderId) {
        try {
            OrderStatus orderStatus = orderService.getOrderStatus(orderId);
            return ResponseUtil.successResponse(orderStatus, "Order status retrieved successfully");
        } catch (OrderNotFoundException e) {
            return ResponseUtil.notFoundResponse(e.getMessage());
        }
        catch (Exception e) {
            return ResponseUtil.badRequestResponse("Error retrieving order status: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Update order status",
            description = "Updates the status of an existing order"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status update request")
    })
    @SecurityRequirement(name = "bearerAuth")
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

    @Operation(
            summary = "Delete order",
            description = "Deletes an order from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseUtil.successResponse(null, "Order deleted successfully");
        } catch (OrderNotFoundException e) {
            return ResponseUtil.notFoundResponse(e.getMessage());
        }
    }

    @Operation(
            summary = "Get orders by status",
            description = "Retrieves all orders with a specific status"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved orders",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Order.class)))
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseUtil.successResponse(orders, "Orders retrieved successfully");
    }


    @Operation(
            summary = "Get orders by preparer",
            description = "Retrieves all orders prepared by a specific user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved orders"),
            @ApiResponse(responseCode = "404", description = "Preparer not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/preparedBy/{userId}")
    public ResponseEntity<?> getOrdersByPreparedBy(@PathVariable Long userId) {
        User preparedBy = userService.findUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + userId + " not found"));
        List<Order> orders = orderService.getOrdersByPreparedBy(preparedBy);
        return ResponseUtil.successResponse(orders, "Orders retrieved successfully");
    }


    @Operation(
            summary = "Update order preparer",
            description = "Updates the user assigned to prepare an order"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order preparer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Order or user not found"),
            @ApiResponse(responseCode = "400", description = "Invalid update request")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{orderId}/preparedBy")
    public ResponseEntity<?> updateOrderPreparedBy(
            @PathVariable Long orderId,
            @RequestBody UpdatePreparedByDto dto) {
        try {
            Order updatedOrder = orderService.updateOrderPreparedBy(orderId, dto.getUserName());
            return ResponseUtil.successResponse(updatedOrder, "Order preparedBy updated successfully");
        } catch (OrderNotFoundException | EntityNotFoundException e) {
            return ResponseUtil.notFoundResponse(e.getMessage());
        }
        catch (Exception e) {
            return ResponseUtil.badRequestResponse("Error updating order preparedBy: " + e.getMessage());
        }
    }


    // For Presentation
    @PostMapping("/presentation")
    public ResponseEntity<?> createNewOrderNumberToTheBoard(){
        try {
            return ResponseUtil.successResponse(orderService.forceAddOrderNumberToTheBoard(), "Order created successfully");
        } catch (Exception e) {
            return ResponseUtil.badRequestResponse("Error creating order: " + e.getMessage());
        }
    }

}
