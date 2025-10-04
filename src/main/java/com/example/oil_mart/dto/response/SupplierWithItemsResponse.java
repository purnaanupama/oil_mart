package com.example.oil_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class SupplierWithItemsResponse {
    private Long id;
    private String supplierName;
    private String supplierPhone;
    private String supplierAddress;
    private LocalDateTime createdAt;
    private List<ItemResponseForSupplier> items;
}
