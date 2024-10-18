package sample.test.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Boolean available;
    private String category;
    private String photoUrl;
}
