package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class GRNItemSaveRequest {
    private Long grnId;
    private Long itemId;
    private Long supplierId;  // Changed from supplier_name
    private Integer quantity;  // Changed to Integer
    private Double unitPrice;
    private Double totalPrice;
    private String createdAt;
}