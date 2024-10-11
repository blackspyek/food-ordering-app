package com.food.backend.controller;

import com.food.backend.dto.MenuItemDto;
import com.food.backend.model.Enums.Category;
import com.food.backend.model.MenuItem;
import com.food.backend.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

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


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenuItem(
            @PathVariable("id") int id
    ) {
        try{
            menuItemService.deleteMenuItem(id);
            return ResponseEntity.ok("MenuItem" + id + " deleted successfully");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Failed to delete menu item: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateMenuItem(
            @PathVariable("id") int id,
            @RequestBody MenuItemDto menuItem
    ) {
        try{
            return ResponseEntity.ok(menuItemService.patchUpdate(id, menuItem));
        }
        catch (MethodArgumentNotValidException e) {
            assert e.getBindingResult() != null;
            return ResponseEntity.badRequest().body("Validation failed: " + e.getBindingResult().getAllErrors());
        }
            catch (Exception e){
            return ResponseEntity.badRequest().body("Failed to update menu item: " + e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createMenuItem(
            @Valid
            @RequestBody MenuItemDto menuItemDto
    ) {
        try{
            MenuItem newMenuItem = menuItemService.createNewMenuItem(menuItemDto);
            return ResponseEntity.ok(newMenuItem);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Failed to update menu item: " + e.getMessage());
        }
    }



}
