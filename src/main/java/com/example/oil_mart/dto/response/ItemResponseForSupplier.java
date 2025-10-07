package com.example.oil_mart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemResponseForSupplier {
    private Long id;
    private String itemCode;
    private String itemDescription;
    private double wholesalePrice;
    private double retailPrice;
}
