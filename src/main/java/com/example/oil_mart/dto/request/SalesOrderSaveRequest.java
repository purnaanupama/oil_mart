package com.example.oil_mart.dto.request;

import com.example.oil_mart.model.Customer;
import lombok.Data;
import java.util.List;

@Data
public class SalesOrderSaveRequest {
    private String salesOrderNo;
    private String salesOrderType;
    private String receiptNo;
    private Double totalAmount;
    private Long customerId;
    private String note;
    private String createdAt;
    private List<SalesOrderItemSaveRequest> items; // Add this if you want to save items with the order
}