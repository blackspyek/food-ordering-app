package com.food.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Map;


@Data
@Builder
@Schema(description = "Data Transfer Object for Sales Report")
public class ReportDto{
    @Schema(description = "Total number of orders", example = "150")
    private Number totalOrders;

    @Schema(description = "Total amount of sales", example = "2500.50")
    private Double totalAmount;

    @Schema(description = "Average order amount", example = "16.67")
    private Double averageAmount;

    @Schema(description = "Sales breakdown by category")
    private Map<String, Number> salesByCategory;
}
