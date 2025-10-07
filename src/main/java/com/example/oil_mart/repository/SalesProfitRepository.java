package com.example.oil_mart.repository;

import com.example.oil_mart.model.SalesProfit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalesProfitRepository extends JpaRepository<SalesProfit, Long> {
    Optional<SalesProfit> findBySalesOrderNo(String salesOrderNo);

    void deleteBySalesOrderNo(String salesOrderNo);
}
