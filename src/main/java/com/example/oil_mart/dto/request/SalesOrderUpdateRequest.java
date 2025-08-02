package com.example.oil_mart.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class SalesOrderUpdateRequest {
    private Long id;
    private String salesOrderNo;
    private String salesOrderType;
    private Double totalAmount;
    private Long customerId; // Assuming you want to update the customer by ID
    private String note;
    private String createdAt;
    private List<SalesOrderItemSaveRequest> items;
}
