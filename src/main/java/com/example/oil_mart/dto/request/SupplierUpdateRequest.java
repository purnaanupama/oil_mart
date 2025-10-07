package com.example.oil_mart.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierUpdateRequest {
    private Long id;
    private String supplierName;
    private String supplierPhone;
    private String supplierAddress;
}
