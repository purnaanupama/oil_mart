package com.example.oil_mart.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "SUPPLIER")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CUSTOMER_CODE", unique = true, nullable = false, length = 40)
    private  String customer_code;

    @Column(name = "CUSTOMER_NAME", nullable = false, length = 100)
    private String customer_name;

    @Column(name = "CUSTOMER_PHONE", length = 15)
    private String customer_phone;

    @Column(name = "Created_At", nullable = false)
    private String createdAt;
}
