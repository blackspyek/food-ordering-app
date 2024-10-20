package com.food.backend.dto;

import lombok.*;

import java.util.Map;


@Data
@Builder
public class ReportDto{
    private Number totalOrders;
    private Double totalAmount;
    private Double averageAmount;
    private Map<String, Number> salesByCategory;
}
