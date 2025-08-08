package com.example.oil_mart.service;


import com.example.oil_mart.dto.request.SalesOrderItemUpdateRequest;
import com.example.oil_mart.dto.request.SalesOrderSaveRequest;
import com.example.oil_mart.dto.request.SalesOrderUpdateRequest;
import com.example.oil_mart.dto.response.SalesOrderResponse;

import java.util.List;

public interface SalesOrderService {
    SalesOrderResponse saveSalesOrder(SalesOrderSaveRequest request);
    List<SalesOrderResponse> getSalesOrders();
    SalesOrderResponse getSalesOrderById(Long id);
    void deleteSalesOrderById(Long id);
    SalesOrderResponse updateSalesOrder(SalesOrderUpdateRequest request);
    SalesOrderResponse updateSalesOrderItem(SalesOrderItemUpdateRequest request);
    void deleteSalesOrderItemById(Long SalesOrderId,Long id);

}
