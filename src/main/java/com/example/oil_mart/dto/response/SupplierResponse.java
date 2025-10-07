package com.example.oil_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SupplierResponse {
    private Long id;
    private String supplierName;
    private String supplierPhone;
    private String supplierAddress;
    private LocalDateTime createdAt;
}
