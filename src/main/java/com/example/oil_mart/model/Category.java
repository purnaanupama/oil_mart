package com.example.oil_mart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "CATEGORY")
public class Category extends BaseEntity {

    @Column(name = "CATEGORY_NAME", nullable = false, length = 45)
    private String categoryName;

}
