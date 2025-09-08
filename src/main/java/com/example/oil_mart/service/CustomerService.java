package com.example.oil_mart.service;

import com.example.oil_mart.dto.request.CustomerSaveRequest;
import com.example.oil_mart.dto.request.CustomerUpdateRequest;
import com.example.oil_mart.dto.response.CustomerResponse;
import com.example.oil_mart.dto.response.SalesOrderResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponse saveCustomer(CustomerSaveRequest saveRequest);
    CustomerResponse getCustomerById(Long id);
    List<CustomerResponse> getAllCustomers();
    CustomerResponse updateCustomer(CustomerUpdateRequest updateRequest);
    CustomerResponse deleteCustomerById(Long id);
    List<SalesOrderResponse> getCustomerCreditOrders(Long id);
}
