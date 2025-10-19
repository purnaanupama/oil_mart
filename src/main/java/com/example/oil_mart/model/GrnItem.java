package com.example.oil_mart.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "GRN_ITEM")
public class GrnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ITEM_ID", nullable = false)
    private Long itemId;

    @Column(name = "ITEM_CODE", nullable = true)
    private String itemCode;

    @Column(name = "SUPPLIER_ID", nullable = false)
    private Long supplierId;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "UNIT_PRICE", nullable = false)
    private Double unitPrice;

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private Double totalAmount;

    @Column(name = "SUPPLIER_NAME", nullable = true)
    private String supplierName;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRN_ID", nullable = false)
    private Grn grn;
}