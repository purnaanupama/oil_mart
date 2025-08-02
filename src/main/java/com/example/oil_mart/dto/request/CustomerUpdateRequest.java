package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class CustomerUpdateRequest {
    private Long id; // This is the identifier for the customer to be updated
    private String customerNo;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String customerNic;
    private String createdAt; // This should be set to the current date/time in the service layer
}
