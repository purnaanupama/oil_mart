package com.example.oil_mart.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class GRNSaveRequest {
    private String grnNumber;
    private String invoiceNumber;
    private double totalAmount;
    private String createdAt; // Timestamp of creation
    private List<GRNItemSaveRequest> items;
}