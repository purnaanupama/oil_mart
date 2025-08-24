package com.example.oil_mart.dto.response;

import lombok.Data;

@Data
public class SalesOrderItemResponse {
    private Long id;
    private Long itemId;
    private Integer quantity;
    private Double quantityLiters;
    private Integer quantityMilliliters;
    private Double soItemUnitPrice;
    private Boolean isLoose;
    private Double soItemTotalAmount;
    private String createdAt;
}