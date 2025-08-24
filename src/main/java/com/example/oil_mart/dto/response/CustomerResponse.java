package com.example.oil_mart.dto.response;

import lombok.Data;

@Data
public class CustomerResponse {
    private Long id;
    private String customerCode;
    private String customerName;
    private String customerPhone;
    private String createdAt; // This should be formatted as needed, e.g., "yyyy-MM-dd HH:mm:ss"
}
