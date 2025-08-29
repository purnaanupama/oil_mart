package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class ReturnItemReq {
    private Long itemId;
    private int quantity;
    private Double quantityLitres;
    private Double quantityMiliLitres;
    private Boolean isLoose;
    // getters and setters
}
