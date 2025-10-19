package com.example.oil_mart.dto.response;

import lombok.Data;

@Data
public class GRNItemResponse {
    private Long id;
    private Long itemId;
    private Long supplierId;  // Added
    private String itemCode;
    private Integer quantity;
    private Double unitPrice;
    private Double totalAmount;
    private String supplierName;
    private String createdAt;
}