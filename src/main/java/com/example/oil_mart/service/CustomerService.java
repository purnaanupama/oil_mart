package com.example.oil_mart.service;

import com.example.oil_mart.dto.request.CustomerSaveRequest;
import com.example.oil_mart.dto.request.CustomerUpdateRequest;
import com.example.oil_mart.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponse saveCustomer(CustomerSaveRequest saveRequest);
    List<CustomerResponse> getAllCustomers();
    CustomerResponse getCustomerById(Long id);
    CustomerResponse updateCustomer(CustomerUpdateRequest updateRequest);
    CustomerResponse deleteCustomerById(Long id);
}
