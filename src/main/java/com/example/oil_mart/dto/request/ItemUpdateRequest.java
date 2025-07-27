package com.example.oil_mart.dto.request;

import com.example.oil_mart.enums.Status;
import lombok.Data;

@Data
public class ItemUpdateRequest {

    private Integer id;

    private String itemCode;

    private String itemDescription;

    private double wholesalePrice;

    private double retailPrice;

    private Integer itemBrand;

    private Status status;

    private String modifiedBy;

    private String packSize;

    private String packUnit;

}
