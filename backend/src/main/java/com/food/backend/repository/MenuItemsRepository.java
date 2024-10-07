package com.food.backend.repository;

import com.food.backend.model.Enums.Category;
import com.food.backend.model.MenuItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuItemsRepository extends CrudRepository<MenuItem, Long> {
    Optional<MenuItem> findByNameIgnoreCase(String name);
    List<MenuItem> findByAvailable(boolean available);

    List<MenuItem> findByCategory(Category category);

    Optional<MenuItem> findById(int id);


}
