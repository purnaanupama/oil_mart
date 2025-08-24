package com.example.oil_mart.dto.response;

import lombok.Data;

@Data
public class GRNItemResponse {
    private Long id;
    private Long itemId;
    private String itemCode;
    private int quantity;
    private double unitPrice;
    private double totalAmount;
    private String supplierName;
    private String createdAt;
}