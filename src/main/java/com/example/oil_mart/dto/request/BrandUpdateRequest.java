package com.example.oil_mart.dto.request;

import com.example.oil_mart.enums.Status;
import lombok.Data;

@Data
public class BrandUpdateRequest {

    private Integer id;

    private String brandName;

    private Status status;

    private String modifiedBy;

}
