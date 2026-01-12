package com.food.backend.service.interfaces;

import com.food.backend.dto.MenuItemDto;
import com.food.backend.model.Enums.Category;
import com.food.backend.model.MenuItem;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Optional;

public interface IMenuItemService {
    Optional<MenuItem> findByNameIgnoreCase(String name);
    List<MenuItem> findByAvailable(boolean available);
    List<MenuItem> findByCategory(Category category);
    List<MenuItem> findAll();
    MenuItem createNewMenuItem(MenuItemDto menuItemDto) throws BadRequestException;
    MenuItem updateMenuItem(int id, MenuItemDto menuItemDto) throws BadRequestException;
    void deleteMenuItem(int id);
    Optional<MenuItem> findById(Long id);
    MenuItem changeAvailability(Long id);
}
