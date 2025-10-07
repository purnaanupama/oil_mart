package com.example.oil_mart.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "RETURNS")
public class Return {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "RO_NUMBER", nullable = false, length = 45, unique = true)
    private String RONumber;

    @Column(name = "CREATED_AT", nullable = false)
    private String createdAt;

    @OneToMany(mappedBy = "returnRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Return_item> items = new ArrayList<>();
}
