package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class SalesOrderItemUpdateRequest {
    private Integer quantity;
    private Double quantityLiters;
    private Double quantityMilliliters; // Should be Double, not Integer
    private Boolean isLoose;
    private Double soItemUnitPrice;
    private Double soItemTotalAmount;
    private String updatedAt;
}
