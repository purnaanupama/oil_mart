package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class ItemSaveRequest {

    private String itemCode;

    private String itemDescription;

    private double wholesalePrice;

    private double retailPrice;

    private Integer itemBrand;

    private String createdBy;

}
