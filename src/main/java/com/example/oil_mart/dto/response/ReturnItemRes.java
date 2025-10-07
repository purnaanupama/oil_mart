package com.example.oil_mart.dto.response;

import lombok.Data;

@Data
public class ReturnItemRes {
    private Long id;
    private Long itemId;
    private Boolean isLoose;
    private Integer quantity;
    private Double quantityLitres;
    private Double quantityMiliLitres;
    // getters and setters
}

