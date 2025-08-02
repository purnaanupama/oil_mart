package com.example.oil_mart.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "CUSTOMER")
public class Customer{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CUSTOMER_No", unique = true, nullable = false, length = 40)
    private  String customerNo;

    @Column(name = "CUSTOMER_NAME", nullable = false, length = 100)
    private String customer_name;

    @Column(name = "CUSTOMER_ADDRESS", length = 255)
    private String customer_address;

    @Column(name = "CUSTOMER_PHONE", length = 15)
    private String customer_phone;

    @Column(name = "CUSTOMER_NIC", length = 12)
    private String customer_nic;

    @Column(name = "Created_At", nullable = false)
    private String createdAt;
}
