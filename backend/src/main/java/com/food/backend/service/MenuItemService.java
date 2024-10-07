package com.food.backend.service;

import com.food.backend.model.Enums.Category;
import com.food.backend.model.MenuItem;
import com.food.backend.repository.MenuItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

}
