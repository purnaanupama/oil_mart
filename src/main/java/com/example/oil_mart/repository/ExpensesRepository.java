package com.example.oil_mart.repository;


import com.example.oil_mart.model.Expenses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpensesRepository extends JpaRepository<Expenses, Long> {
    Optional<Expenses> findByExpenseName(String expenseName);
    boolean existsByExpenseName(String expenseName);

}