package com.example.oil_mart.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "SALES_ORDER")
public class Sales_Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SALES_ORDER_NO", unique = true, nullable = false, length = 40)
    private String salesOrderNo;

    @Column(name = "SALES_ORDER_TYPE", nullable = false, length = 20)
    private String salesOrderType;

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private Double totalAmount;

    @Column(name = "NOTE", length = 255)
    private String note;

    @Column(name = "Created_At", nullable = false)
    private String createdAt;

    @Column(name = "STATUS", nullable = true)
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    private Customer customer;
}
