package com.food.backend.repository;

import com.food.backend.model.OrderItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {



}
