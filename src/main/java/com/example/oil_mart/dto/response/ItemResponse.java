package com.example.oil_mart.dto.response;

import com.example.oil_mart.enums.Status;
import lombok.Data;

import java.util.Date;

@Data
public class ItemResponse {

    private Integer id;

    private String itemCode;

    private String itemDescription;

    private String packSize;

    private String packUnit;

    private double wholesalePrice;

    private double retailPrice;

    private double availableStock;

    private double stockInLiters;

    private double stockInMillilitres;

    private BrandResponse itemBrand;

    private Status status;

    private String createdBy;

    private Date createdDateTime;

    private String modifiedBy;

    private Date modifiedDateTime;

}
