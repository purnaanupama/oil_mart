package com.example.oil_mart.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ReturnRes {
    private Long id;
    private String RONumber;
    private String createdAt;
    private List<ReturnItemRes> items;
    // getters and setters
}

