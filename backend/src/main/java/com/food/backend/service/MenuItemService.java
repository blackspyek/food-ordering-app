package com.food.backend.service;

import com.food.backend.dto.MenuItemDto;
import com.food.backend.model.Enums.Category;
import com.food.backend.model.MenuItem;
import com.food.backend.repository.MenuItemsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MenuItemService {
    private final MenuItemsRepository menuItemsRepository;

    public MenuItemService(MenuItemsRepository menuItemsRepository) {
        this.menuItemsRepository = menuItemsRepository;
    }

    public Optional<MenuItem> findByNameIgnoreCase(String name) {
        return menuItemsRepository.findByNameIgnoreCase(name);
    }

    public List<MenuItem> findByAvailable(boolean available) {
        return menuItemsRepository.findByAvailable(available);
    }

    public List<MenuItem> findByCategory(Category category) {
        return menuItemsRepository.findByCategory(category);
    }

    public List<MenuItem> findAll() {
        return (List<MenuItem>) menuItemsRepository.findAll();
    }

    public MenuItem createNewMenuItem(MenuItemDto menuItemDto) throws BadRequestException {
        checkIfCategoryIsValid(getCategoryNameFromDto(menuItemDto));
        if (menuItemsRepository.findByNameIgnoreCase(menuItemDto.getName()).isPresent()) {
            throw new IllegalArgumentException("Menu item with name " + menuItemDto.getName() + " already exists");
        }
        MenuItem newMenuItem = createMenuItemObject(menuItemDto);
        try {
            menuItemsRepository.save(newMenuItem);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create new menu item", e);
        }
        return newMenuItem;
    }

    private static MenuItem createMenuItemObject(MenuItemDto menuItemDto) {
        MenuItem newMenuItem = new MenuItem();
        setMenuItemFieldsFromDto(newMenuItem, menuItemDto);
        newMenuItem.setAvailable(true); // By default, a new menu item is available
        return newMenuItem;
    }

    private static String getCategoryNameFromDto(MenuItemDto menuItemDto) throws BadRequestException {
        if (menuItemDto.getCategory() == null) {
            throw new BadRequestException("Category cannot be empty");
        } else {
            return menuItemDto.getCategory().toString();
        }

    }

    private static void checkIfCategoryIsValid(String categoryName){
        Category.valueOf(categoryName);
    }

    public MenuItem updateMenuItem(int id, MenuItemDto menuItemDto) throws BadRequestException {
        checkIfCategoryIsValid(getCategoryNameFromDto(menuItemDto));
        MenuItem menuItemToUpdate = menuItemsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Menu item with id " + id + " does not exist"));

        setMenuItemFieldsFromDto(menuItemToUpdate, menuItemDto);

        return menuItemsRepository.save(menuItemToUpdate);
    }

    private static void setMenuItemFieldsFromDto(MenuItem menuItem, MenuItemDto menuItemDto) {
        menuItem.setName(menuItemDto.getName());
        menuItem.setDescription(menuItemDto.getDescription());
        menuItem.setPrice(menuItemDto.getPrice());
        menuItem.setCategory(menuItemDto.getCategory());
        menuItem.setPhotoUrl(menuItemDto.getPhotoUrl());
    }

    public void deleteMenuItem(int id) {
        Optional<MenuItem> menuItem = menuItemsRepository.findById(id);
        if (menuItem.isEmpty()) {
            throw new IllegalArgumentException("Menu item with id " + id + " does not exist");
        }
        try {
            menuItemsRepository.delete(menuItem.get());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete menu item with id " + id, e);
        }
    }

    public Optional<MenuItem> findById(Long id) {
        return menuItemsRepository.findById(id);
    }
}
