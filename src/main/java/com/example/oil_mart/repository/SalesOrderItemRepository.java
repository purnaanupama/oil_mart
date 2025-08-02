package com.example.oil_mart.repository;

import com.example.oil_mart.dto.response.SalesOrderItemResponse;
import com.example.oil_mart.model.Sales_Order;
import com.example.oil_mart.model.Sales_Order_Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesOrderItemRepository extends JpaRepository<Sales_Order_Item,Long> {
    List<Sales_Order_Item> findBySalesOrderId(Long salesOrderId);
    List<Sales_Order_Item> findAllBySalesOrder(Sales_Order salesOrder);
}
