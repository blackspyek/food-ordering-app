package com.food.backend.dto;

import com.food.backend.model.Enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class MenuItemDto {

    @NotBlank(message = "Name cannot be empty")
    private String name;
    private String description;
    @Positive(message = "Price must be greater than 0")
    private double price;
    private boolean available;
    private Category category;

    @NotBlank(message = "Photo URL cannot be empty")
    private String photoUrl;

}
