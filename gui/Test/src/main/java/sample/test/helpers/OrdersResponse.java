package sample.test.helpers;

import lombok.Getter;
import lombok.Setter;
import sample.test.model.Order;

import java.util.List;

@Setter
@Getter
public class OrdersResponse {
    private List<Order> data;
}
