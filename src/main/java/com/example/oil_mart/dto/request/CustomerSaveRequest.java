package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class CustomerSaveRequest {
    private String customerCode;
    private String customerName;
    private String customerPhone;
    private String createdAt;
}
