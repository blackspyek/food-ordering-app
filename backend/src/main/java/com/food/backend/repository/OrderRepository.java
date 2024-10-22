package com.food.backend.repository;

import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.model.Order;
import com.food.backend.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOrdersByPreparedBy(User preparedBy);
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems")
    List<Order> findAllWithItems();

    Number countOrderByOrderTimeBetweenAndStatus(LocalDateTime start, LocalDateTime end, OrderStatus status);
    @Query("SELECT AVG(o.totalPrice) FROM Order o WHERE o.orderTime BETWEEN :start AND :end AND o.status = :status")
    Double averageTotalPriceByOrderTimeBetweenAndStatus(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") OrderStatus status
    );
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.orderTime BETWEEN :start AND :end AND o.status = :status")
    Double sumTotalPriceByOrderTimeBetweenAndStatus(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") OrderStatus status
    );
//
    @Query("SELECT mi.category, SUM(oi.quantity) " +
            "FROM OrderItem oi " +
            "JOIN oi.item mi " +
            "JOIN oi.order o " +
            "WHERE o.status = :status AND o.orderTime BETWEEN :start AND :end " +
            "GROUP BY mi.category")
    List<Object[]> countItemsSoldByCategory(
            @Param("status") OrderStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT o.status FROM Order o WHERE o.orderId = :orderId")
    Optional<OrderStatus> findStatusById(@Param("orderId") Long orderId);

}
