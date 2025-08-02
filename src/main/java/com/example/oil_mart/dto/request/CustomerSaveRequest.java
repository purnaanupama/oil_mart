package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class CustomerSaveRequest {
    private String customerNo;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String customerNic;
    private String createdAt; // This should be set to the current date/time in the service layer
}
