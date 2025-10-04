package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class SupplierItemSaveRequest {
    private Long supplierId;
    private Long itemId;
}
