package com.food.backend.repository;

import com.food.backend.model.Enums.OrderStatus;
import com.food.backend.model.Enums.OrderType;
import com.food.backend.model.Order;
import com.food.backend.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOrdersByOrderType(OrderType orderType);
    List<Order> getOrdersByPreparedBy(User preparedBy);
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems")
    List<Order> findAllWithItems();

}
