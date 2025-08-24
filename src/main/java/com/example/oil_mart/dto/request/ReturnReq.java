package com.example.oil_mart.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ReturnReq {
    private String RONumber;
    private Double totalAmount;
    private String createdAt;
    private List<ReturnItemReq> items;
    // getters and setters
}

