package com.example.android.dto;
import com.example.android.models.OrderItem;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Data {
    private int orderId;
    private String preparedBy;
    private String status;
    private String orderType;
    private double totalPrice;
    private String orderTime;
    private String boardCode;
    private String email;
    private List<OrderItem> orderItems;


}