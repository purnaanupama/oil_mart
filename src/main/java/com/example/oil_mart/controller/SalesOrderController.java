package com.example.oil_mart.controller;

import com.example.oil_mart.dto.request.SalesOrderItemUpdateRequest;
import com.example.oil_mart.dto.request.SalesOrderSaveRequest;
import com.example.oil_mart.dto.request.SalesOrderUpdateRequest;
import com.example.oil_mart.dto.response.SalesOrderResponse;
import com.example.oil_mart.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/sales-order")
public class SalesOrderController {
    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping
    public SalesOrderResponse saveSalesOrder(@RequestBody SalesOrderSaveRequest request) {
        System.out.println("this works");
        return salesOrderService.saveSalesOrder(request);
    }

    @PutMapping("/credit-payment-status/{id}")
    public SalesOrderResponse updateCreditPaymentStatus(@PathVariable Long id) {
        return salesOrderService.updateCreditPaymentStatus(id);
    }

    @GetMapping
    public List<SalesOrderResponse> getSalesOrders() {
        return salesOrderService.getSalesOrders();
    }

    @GetMapping("/{id}")
    public SalesOrderResponse getSalesOrderById(@PathVariable Long id) {
        return salesOrderService.getSalesOrderById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteSalesOrderById(@PathVariable Long id) {
        salesOrderService.deleteSalesOrderById(id);
    }


    @DeleteMapping("/{salesOrderId}/item/{itemId}")
    public void deleteSalesOrderItemById(@PathVariable Long salesOrderId, @PathVariable Long itemId) {
        salesOrderService.deleteSalesOrderItemById(salesOrderId, itemId);
    }

    @GetMapping("/last-order-number")
    public String getLastOrderNumber() {
        // Assuming the service has a method to get the last order number
        return salesOrderService.getLastOrderNumber();
    }
}