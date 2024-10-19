package com.food.backend.controller;

import com.food.backend.dto.MenuItemDto;
import com.food.backend.model.Enums.Category;
import com.food.backend.model.MenuItem;
import com.food.backend.service.MenuItemService;
import com.food.backend.utils.classes.ResponseUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/{id}")
    public ResponseEntity<?> getMenuItems(
            @PathVariable("id") Long id
    ) {
        try{
            MenuItem menuItem = menuItemService.findById(id).orElseThrow(() -> new EntityNotFoundException("Menu item with id " + id + " not found"));
            return ResponseUtil.successResponse(menuItem, "Menu item retrieved successfully");
        }
        catch (EntityNotFoundException e){
            return ResponseUtil.notFoundResponse(e.getMessage());
        }
        catch (Exception e){
            return ResponseUtil.badRequestResponse("Error retrieving menu item: " + e.getMessage());
        }
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuItem(
            @PathVariable("id") int id,
            @Valid
            @RequestBody MenuItemDto menuItem
    ) {
        try{
            MenuItem patchedMenuItem = menuItemService.updateMenuItem(id, menuItem);
            return ResponseEntity.ok(patchedMenuItem);
        }
        catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menu item with id " + id + " does not exist");
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
