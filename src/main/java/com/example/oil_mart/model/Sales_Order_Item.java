package com.example.oil_mart.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "SALES_ORDER_ITEM")
public class Sales_Order_Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "SO_ITEM_UNIT_PRICE", nullable = false)
    private Double soItemUnitPrice;

    @Column(name = "SO_ITEM_TOTAL_AMOUNT", nullable = false)
    private Double soItemTotalAmount;

    @Column(name = "Created_At", nullable = false)
    private String createdAt;

    @ManyToOne
    @JoinColumn(name = "SALES_ORDER_ID", nullable = false)
    private Sales_Order salesOrder;

    @ManyToOne
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item;
}
