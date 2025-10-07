package com.example.oil_mart.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ITEM")
public class Item extends BaseEntity {

    @Column(name = "ITEM_CODE", nullable = false, length = 45)
    private String itemCode;

    @Column(name = "ITEM_DESCRIPTION", nullable = true, length = 100)
    private String itemDescription;

    @Column(name = "PACK_SIZE", length = 15)
    private String packSize = "N/A";

    @Column(name = "PACK_UNIT", length = 15)
    private String packUnit = "unit";

    @Column(name = "WHOLESALE_PRICE", nullable = false)
    private double wholesalePrice;

    @Column(name = "RETAIL_PRICE", nullable = false)
    private double retailPrice;

    @Column(name="AVAILABLE_STOCK", nullable = false, columnDefinition = "double default 0.0")
    private double availableStock;

    @Column(name="STOCK_IN_LITERS", nullable = false)
    private double stockInLiters;

    @Column(name="STOCK_IN_MILLILITRES", nullable = false)
    private double stockInMillilitres;

    @ManyToOne
    @JoinColumn(name = "BRAND_ID", referencedColumnName = "ID")
    private Brand itemBrand;
}
