package com.example.oil_mart.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class SalesOrderResponse {
    private Long id;
    private String salesOrderNo;
    private String salesOrderType;
    private Double totalAmount;
    private Long customerId;
    private String note;
    private String createdAt;
    private Boolean status;
    private List<SalesOrderItemResponse> items;
}