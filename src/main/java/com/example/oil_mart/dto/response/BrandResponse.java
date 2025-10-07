package com.example.oil_mart.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class BrandResponse {
    private Integer id;
    private String brandName;
    private String createdBy;
    private Date createdDateTime;
}
