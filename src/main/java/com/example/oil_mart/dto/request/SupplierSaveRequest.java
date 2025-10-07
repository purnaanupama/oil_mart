package com.example.oil_mart.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierSaveRequest {
    private String supplierName;
    private String supplierPhone;
    private String supplierAddress;
}
