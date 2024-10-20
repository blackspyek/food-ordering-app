package com.example.android.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDto {
    private String email;
    private String orderType;
    private List<OrderItemDto> orderItems;

}