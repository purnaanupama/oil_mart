package com.example.oil_mart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "BRAND")
public class Brand extends BaseEntity{

    @Column(name = "BRAND_NAME", nullable = false, length = 45)
    private String brandName;

}
