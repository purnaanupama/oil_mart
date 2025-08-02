package com.example.oil_mart.dto.response;

import lombok.Data;

@Data
public class SalesOrderItemResponse {
    private Long id;
    private Long itemId;
    private Integer quantity;
    private Double soItemUnitPrice;
    private Double soItemTotalAmount;
    private String createdAt;
}