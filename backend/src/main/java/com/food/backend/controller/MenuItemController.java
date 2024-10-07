package com.food.backend.controller;

import com.food.backend.model.Enums.Category;
import com.food.backend.model.MenuItem;
import com.food.backend.model.User;
import com.food.backend.service.MenuItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuItemController {
    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping("/")
    public ResponseEntity<List<MenuItem>> getMenuItems() {
        return ResponseEntity.ok(menuItemService.findAll());
    }

    @GetMapping("/available")
    public ResponseEntity<List<MenuItem>> getAvailableMenuItems() {
        return ResponseEntity.ok(menuItemService.findByAvailable(true));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByCategory(
            @PathVariable("category") Category category
    ) {
        return ResponseEntity.ok(menuItemService.findByCategory(category));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<MenuItem> getMenuItemByName(
            @PathVariable("name") String name
    ) {
        MenuItem menuItem = menuItemService.findByNameIgnoreCase(name).orElse(null);
        return menuItem == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(menuItem);
    }

}
