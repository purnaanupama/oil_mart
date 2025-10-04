package com.example.oil_mart.repository;

import com.example.oil_mart.model.Customer;
import com.example.oil_mart.model.Sales_Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalesOrderRepository extends JpaRepository<Sales_Order,Long> {
    Sales_Order findTopByOrderByIdDesc();

    boolean existsByCustomerId(Long customerId);

    List<Sales_Order> findByCustomer(Customer customer);

    Optional<Sales_Order> findBySalesOrderNo(String salesOrderNo);
    // New methods for search functionality
    List<Sales_Order> findByCustomerAndSalesOrderType(Customer customer, String salesOrderType);

    @Query("SELECT so FROM Sales_Order so WHERE so.customer = :customer " +
            "AND so.salesOrderType = 'CREDIT' " +
            "AND (LOWER(so.salesOrderNo) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(so.createdAt) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR CAST(so.totalAmount AS string) LIKE CONCAT('%', :searchText, '%'))")
    List<Sales_Order> findByCreditOrdersWithSearch(@Param("customer") Customer customer,
                                                   @Param("searchText") String searchText);
}
