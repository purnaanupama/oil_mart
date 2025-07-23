package com.example.oil_mart.dto.response;

import com.example.oil_mart.enums.Status;

import java.util.Date;

public class ItemResponse {

    private Integer id;

    private String itemCode;

    private String itemDescription;

    private String packSize;

    private String packUnit;

    private double wholesalePrice;

    private double retailPrice;

    private Integer itemBrand;

    private Integer itemCategory;

    private Status status;

    private String createdBy;

    private Date createdDateTime;

    private String modifiedBy;

    private Date modifiedDateTime;

}
