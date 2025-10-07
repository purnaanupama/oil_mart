package com.example.oil_mart.repository;

import com.example.oil_mart.model.ExpensesLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpensesLogRepository extends JpaRepository<ExpensesLog, Long> {
}