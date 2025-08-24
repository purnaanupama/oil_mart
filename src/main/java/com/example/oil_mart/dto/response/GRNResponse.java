package com.example.oil_mart.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class GRNResponse {
    private Integer id;
    private String grnNumber;
    private String invoiceNumber;
    private double totalAmount;
    private String createdAt; // Timestamp of creation
    private List<GRNItemResponse> items;
}