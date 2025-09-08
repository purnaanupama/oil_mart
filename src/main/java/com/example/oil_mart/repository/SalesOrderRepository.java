package com.example.oil_mart.repository;

import com.example.oil_mart.model.Customer;
import com.example.oil_mart.model.Sales_Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalesOrderRepository extends JpaRepository<Sales_Order,Long> {
    Sales_Order findTopByOrderByIdDesc();

    boolean existsByCustomerId(Long customerId);

    List<Sales_Order> findByCustomer(Customer customer);

    Optional<Sales_Order> findBySalesOrderNo(String salesOrderNo);

}
