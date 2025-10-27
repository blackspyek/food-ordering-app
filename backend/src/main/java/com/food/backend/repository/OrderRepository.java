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
import java.util.UUID; // Upewnij się, że ten import jest

@Repository
public interface OrderRepository extends CrudRepository<Order, UUID> { // Tu jest OK (UUID)

    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOrdersByPreparedBy(User preparedBy);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems oi LEFT JOIN FETCH oi.item WHERE o.status = :status ORDER BY o.orderTime ASC")
    List<Order> findByStatusWithItems(@Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems")
    List<Order> findAllWithItems();

    // ZMIANA 1: Sortowanie po UUID nie ma sensu chronologicznego.
    // Zmieniamy sortowanie na 'orderTime' (czas zamówienia), żeby mieć najnowsze na górze.
    // Zmieniłem też nazwę metody, żeby pasowała do logiki.
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems ORDER BY o.orderTime DESC")
    List<Order> findAllWithItemsOrderByOrderTimeDesc();


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

    // ZMIANA 2: Zmieniono typ parametru z Long na UUID
    @Query("SELECT o.status FROM Order o WHERE o.orderId = :orderId")
    Optional<OrderStatus> findStatusById(@Param("orderId") UUID orderId);

    @Query("SELECT o FROM Order o WHERE o.orderedBy.id = :userId ORDER BY o.orderTime DESC")
    List<Order> findTop10ByOrderedByIdOrderByOrderTimeDesc(Long userId);

}