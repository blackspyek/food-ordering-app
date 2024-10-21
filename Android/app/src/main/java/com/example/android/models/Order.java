package com.example.android.models;
import com.example.android.dto.OrderItemDto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable{
    private Long orderId;
    private String status;
    private String orderType;
    private Double totalPrice;
    private LocalDateTime orderTime;
    private String boardCode;
    private String email;
    private List<MenuItem> orderItems;
}