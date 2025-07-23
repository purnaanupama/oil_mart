package com.example.oil_mart.dto.request;

import com.example.oil_mart.enums.Status;
import lombok.Data;

@Data
public class CategoryUpdateRequest {

    private Integer id;

    private String categoryName;

    private Status status;

    private String modifiedBy;

}
