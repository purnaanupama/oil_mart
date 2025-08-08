package com.example.oil_mart.repository;

import com.example.oil_mart.model.Sales_Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesOrderRepository extends JpaRepository<Sales_Order,Long> {
}
