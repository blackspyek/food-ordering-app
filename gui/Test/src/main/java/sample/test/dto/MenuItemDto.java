package sample.test.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sample.test.model.Category;

@Setter
@Getter
@AllArgsConstructor
public class MenuItemDto {
    private String name;
    private String description;
    private double price;
    private boolean available;
    private Category category;
    private String photoUrl;
}
