package com.example.oil_mart.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "SALES_PROFIT")
public class SalesProfit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SALES_ORDER_NO", unique = true, nullable = false, length = 40)
    private String salesOrderNo;

    @Column(name = "SALES_ORDER_TYPE", nullable = false, length = 20)
    private String salesOrderType;

    @Column(name = "TOTAL_PROFIT_AMOUNT", nullable = false)
    private Double totalProfitAmount;

    @Column(name = "Created_At", nullable = false)
    private String createdAt;
}
