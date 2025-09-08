package com.example.oil_mart.dto.response;

import lombok.Data;

@Data
public class CustomerResponse {
    private Long id;
    private String customerCode;
    private String customerName;
    private String customerPhone;
    private String createdAt;
}
