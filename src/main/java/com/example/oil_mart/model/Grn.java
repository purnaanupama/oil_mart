package com.example.oil_mart.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "GRN")
public class Grn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grnId;

    @Column(name = "GRN_NUMBER", nullable = false, length = 45, unique = true)
    private String grnNumber;

    @Column(name = "INVOICE_NUMBER", nullable = false)
    private String invoiceNumber;

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private Double totalAmount;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrnItem> items = new ArrayList<>();
}