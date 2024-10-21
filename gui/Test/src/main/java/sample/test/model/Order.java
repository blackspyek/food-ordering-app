package sample.test.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class Order {
    private Long orderId;
    private User preparedBy;
    private String status;
    private String orderType;
    private Double totalPrice;
    private String orderTime;
    private String boardCode;
    private String email;
    private List<OrderItem> orderItems;
}
