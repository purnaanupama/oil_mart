package com.example.oil_mart.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "RETURN_ITEM")
public class Return_item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ITEM_ID", nullable = false)
    private Long itemId;

    @Column(name = "Is_Loose" , nullable = false )
    private Boolean isLoose;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "Quantity_In_L", nullable = false)
    private Double quantityLitres;

    @Column(name = "Quantity_In_ML", nullable = false)
    private Double quantityMiliLitres;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RETURN_ID", nullable = false)
    private Return returnRecord;
}
