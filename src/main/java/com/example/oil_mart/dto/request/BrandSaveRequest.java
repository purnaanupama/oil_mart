package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class BrandSaveRequest {
    private String brandName;
    private String createdBy; // required for BaseEntity
}
