package com.example.oil_mart.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "SUPPLIER_ITEM")
@Getter
@Setter
@NoArgsConstructor
public class SupplierItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Supplier foreign key
    @ManyToOne
    @JoinColumn(name = "SUPPLIER_ID", nullable = false)
    private Supplier supplier;

    // Item foreign key
    @ManyToOne
    @JoinColumn(name = "ITEM_ID", nullable = false)
    private Item item;
}
