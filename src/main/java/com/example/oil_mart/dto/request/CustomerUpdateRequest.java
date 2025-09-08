package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class CustomerUpdateRequest {
    private Long id; // This is the identifier for the customer to be updated;
    private String customerName;
    private String customerPhone;
}
