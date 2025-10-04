package com.example.oil_mart.dto.response;

import lombok.Data;

@Data
public class SupplierItemResponse {
    private Long id;
    private Long supplierId;
    private String supplierName;
    private Long itemId;
    private String itemCode;
    private String itemDescription;
}
