package com.example.oil_mart.dto.response;

import com.example.oil_mart.enums.Status;
import lombok.Data;

import java.util.Date;

@Data
public class CategoryResponse {

    private Integer id;

    private String categoryName;

    private String createdBy;

    private Date createdDateTime;

    private String modifiedBy;

    private Date modifiedDateTime;

    private Status status;

}
