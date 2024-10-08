package com.food.backend.service;

import com.food.backend.dto.MenuItemDto;
import com.food.backend.model.Enums.Category;
import com.food.backend.model.MenuItem;
import com.food.backend.repository.MenuItemsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemService {
    private final MenuItemsRepository menuItemsRepository;

    public MenuItemService(MenuItemsRepository menuItemsRepository) {
        this.menuItemsRepository = menuItemsRepository;
    }


    public Optional<MenuItem> findByNameIgnoreCase(String name){
        return menuItemsRepository.findByNameIgnoreCase(name);
    }
    public List<MenuItem> findByAvailable(boolean available){
        return menuItemsRepository.findByAvailable(available);
    }

    public List<MenuItem> findByCategory(Category category){
        return menuItemsRepository.findByCategory(category);
    }

    public List<MenuItem> findAll(){
        return (List<MenuItem>) menuItemsRepository.findAll();
    }

    public MenuItem createNewMenuItem(
            MenuItemDto menuItemDto
    ){
        try{
            Category.valueOf(menuItemDto.getCategory().toString());
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid category");
        }
        if (menuItemsRepository.findByNameIgnoreCase(menuItemDto.getName()).isPresent()){
            throw new IllegalArgumentException("Menu item with name " + menuItemDto.getName() + " already exists");
        }
        MenuItem newMenuItem = new MenuItem();
        newMenuItem.setName(menuItemDto.getName());
        newMenuItem.setDescription(menuItemDto.getDescription());
        newMenuItem.setPrice(menuItemDto.getPrice());
        newMenuItem.setCategory(menuItemDto.getCategory());
        newMenuItem.setAvailable(true);
        newMenuItem.setPhotoUrl(menuItemDto.getPhotoUrl());

        try {
            menuItemsRepository.save(newMenuItem);
        } catch (Exception e){
            throw new RuntimeException("Failed to create new menu item");
        }
        return newMenuItem;
    }

    public MenuItem patchUpdate(
            int id,
            MenuItemDto menuItemDto
    ){
        Optional<MenuItem> menuItem = menuItemsRepository.findById(id);
        if (menuItem.isEmpty()){
            throw new IllegalArgumentException("Menu item with id " + id + " does not exist");
        }
        MenuItem menuItemToUpdate = menuItem.get();
        if (menuItemDto.getName() != null && !menuItemDto.getName().isEmpty()){
            menuItemToUpdate.setName(menuItemDto.getName());
        }
        if (menuItemDto.getDescription() != null && !menuItemDto.getDescription().isEmpty()){
            menuItemToUpdate.setDescription(menuItemDto.getDescription());
        }
        if (menuItemDto.getPrice() > 0){
            menuItemToUpdate.setPrice(menuItemDto.getPrice());
        }
        if (menuItemDto.getCategory() != null){
            menuItemToUpdate.setCategory(menuItemDto.getCategory());
        }

        try {
            menuItemsRepository.save(menuItemToUpdate);
        } catch (Exception e){
            throw new RuntimeException("Failed to update menu item with id " + id);
        }
        return menuItemToUpdate;
    }

    public void deleteMenuItem(int id){
        Optional<MenuItem> menuItem = menuItemsRepository.findById(id);
        if (menuItem.isEmpty()){
            throw new IllegalArgumentException("Menu item with id " + id + " does not exist");
        }
        try {
            menuItemsRepository.delete(menuItem.get());
        } catch (Exception e){
            throw new RuntimeException("Failed to delete menu item with id " + id);
        }
    }





}
