package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class SalesOrderItemSaveRequest {
    private Long itemId;
    private Integer quantity;
    private Double soItemUnitPrice;
    private Double soItemTotalAmount;
    private String createdAt;
}