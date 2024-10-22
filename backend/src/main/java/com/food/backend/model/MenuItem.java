package com.food.backend.model;

import com.food.backend.model.Enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entity representing a menu item")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the menu item", example = "1")
    private Long Id;

    @Column(nullable = false)
    @Schema(description = "Name of the menu item", example = "Margherita Pizza", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Column(length = 500)
    @Schema(description = "Description of the menu item", example = "Classic Italian pizza with tomato and mozzarella")
    private String description;

    @Column(nullable = false)
    @Schema(description = "Price of the menu item", example = "12.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double price;

    @Column(nullable = false)
    @Schema(description = "Availability status of the menu item", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean available;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Category of the menu item", example = "MAIN_COURSE", requiredMode = Schema.RequiredMode.REQUIRED)
    private Category category;

    @Column(name = "photo_url")
    @Schema(description = "URL of the menu item's photo", example = "http://example.com/images/margherita.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String photoUrl;

}
