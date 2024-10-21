package com.example.android.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private int orderItemId;
    private MenuItem item;
    private int quantity;
    private double totalPrice;
}
