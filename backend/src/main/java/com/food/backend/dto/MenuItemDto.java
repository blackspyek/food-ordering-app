package com.food.backend.dto;

import com.food.backend.model.Enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Schema(description = "Data Transfer Object for Menu Item")
public class MenuItemDto {

    @Schema(description = "Name of the menu item", example = "Margherita Pizza", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @Schema(description = "Description of the menu item", example = "Classic Italian pizza with tomato and mozzarella", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Price of the menu item", example = "12.99", requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive(message = "Price must be greater than 0")
    private double price;

    @Schema(description = "Availability status of the menu item", example = "true")
    private boolean available;

    @Schema(description = "Category of the menu item", example = "MAIN_COURSE", requiredMode = Schema.RequiredMode.REQUIRED)
    private Category category;

    @Schema(description = "URL of the menu item's photo", example = "http://example.com/images/margherita.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Photo URL cannot be empty")
    private String photoUrl;

}
