package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class SalesOrderItemUpdateRequest {
    private Long id;
    private Long itemId;
    private Long salesOrderId;
    private Integer quantity;
    private Double soItemUnitPrice;
    private Double soItemTotalAmount;
    private String updatedAt;
}
