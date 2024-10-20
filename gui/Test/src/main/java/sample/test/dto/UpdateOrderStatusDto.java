package sample.test.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sample.test.model.OrderStatus;

@Getter
@Setter
@AllArgsConstructor
public class UpdateOrderStatusDto {
    private OrderStatus orderStatus;
}
