package sample.test.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class OrderItem {
    private Long orderItemId;
    private MenuItem item;
    private Integer quantity;
    private double totalPrice;

}
