package com.food.backend.controller;

import com.food.backend.dto.MenuItemDto;
import com.food.backend.model.Enums.Category;
import com.food.backend.model.MenuItem;
import com.food.backend.service.MenuItemService;
import com.food.backend.utils.classes.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@Tag(name = "Menu Items", description = "Menu Item Management APIs")
public class MenuItemController {
    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping("/")
    @Operation(
            summary = "Get all menu items",
            description = "Retrieves a list of all menu items in the system"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved menu items")
    public ResponseEntity<List<MenuItem>> getMenuItems() {
        return ResponseEntity.ok(menuItemService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get menu item by ID",
            description = "Retrieves a specific menu item by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu item found"),
            @ApiResponse(responseCode = "404", description = "Menu item not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
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
    @Operation(
            summary = "Get available menu items",
            description = "Retrieves all menu items that are currently available"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved available menu items")
    public ResponseEntity<List<MenuItem>> getAvailableMenuItems() {
        return ResponseEntity.ok(menuItemService.findByAvailable(true));
    }

    @GetMapping("/category/{category}")
    @Operation(
            summary = "Get menu items by category",
            description = "Retrieves all menu items in a specific category"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved menu items for the category")
    public ResponseEntity<List<MenuItem>> getMenuItemsByCategory(
            @PathVariable("category") Category category
    ) {
        return ResponseEntity.ok(menuItemService.findByCategory(category));
    }

    @GetMapping("/name/{name}")
    @Operation(
            summary = "Get menu item by name",
            description = "Retrieves a specific menu item by its name (case insensitive)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu item found"),
            @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    public ResponseEntity<MenuItem> getMenuItemByName(
            @PathVariable("name") String name
    ) {
        MenuItem menuItem = menuItemService.findByNameIgnoreCase(name).orElse(null);
        return menuItem == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(menuItem);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Delete menu item",
            description = "Deletes a menu item by its ID. Requires MANAGER role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu item successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Failed to delete menu item"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @SecurityRequirement(name = "bearerAuth")
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
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Update menu item",
            description = "Updates an existing menu item. Requires MANAGER role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu item successfully updated"),
            @ApiResponse(responseCode = "404", description = "Menu item not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @SecurityRequirement(name = "bearerAuth")
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

    @PatchMapping("/availability/{id}")
    @Operation(
            summary = "Update menu item availability",
            description = "Toggles the availability status of a menu item"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu item availability successfully updated"),
            @ApiResponse(responseCode = "404", description = "Menu item not found"),
            @ApiResponse(responseCode = "400", description = "Failed to update availability")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateMenuItemAvailability(
            @PathVariable("id") Long id
    ) {
        try {
            MenuItem patchedMenuItem = menuItemService.changeAvailability(id);
            return ResponseUtil.successResponse(patchedMenuItem, "Menu item availability updated successfully");

        }
        catch (EntityNotFoundException e){
            return ResponseUtil.notFoundResponse(e.getMessage());
        }
        catch (Exception e){
            return ResponseUtil.badRequestResponse("Failed to update menu item availability: " + e.getMessage());
        }
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Create new menu item",
            description = "Creates a new menu item. Requires MANAGER role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu item successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @SecurityRequirement(name = "bearerAuth")
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
