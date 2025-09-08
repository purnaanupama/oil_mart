package com.example.oil_mart.controller;

import com.example.oil_mart.dto.request.CustomerSaveRequest;
import com.example.oil_mart.dto.request.CustomerUpdateRequest;
import com.example.oil_mart.dto.response.CustomerResponse;
import com.example.oil_mart.dto.response.SalesOrderResponse;
import com.example.oil_mart.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> saveCustomer(@RequestBody CustomerSaveRequest saveRequest) {
        try {
            CustomerResponse saveResponse = customerService.saveCustomer(saveRequest);
            return ResponseEntity.ok(saveResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        try {
            List<CustomerResponse> getAllResponse = customerService.getAllCustomers();
            return ResponseEntity.ok(getAllResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("credit-orders/{customerId}")
    public ResponseEntity<List<SalesOrderResponse>> getCustomerCreditOrders(@PathVariable("customerId") Long customerId) {
        try {
            List<SalesOrderResponse> creditOrders = customerService.getCustomerCreditOrders(customerId);
            return ResponseEntity.ok(creditOrders);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching credit orders for customer ID " + customerId, e);
        }
    }

    @GetMapping("{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable("customerId") Long id) {
        try {
            CustomerResponse getByIdResponse = customerService.getCustomerById(id);
            return ResponseEntity.ok(getByIdResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping
    public ResponseEntity<CustomerResponse> updateCustomer(@RequestBody CustomerUpdateRequest updateRequest) {
        try {
            CustomerResponse updateResponse = customerService.updateCustomer(updateRequest);
            return ResponseEntity.ok(updateResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity<CustomerResponse> deleteCustomerById(@PathVariable("customerId") Long id) {
        try {
            CustomerResponse deleteResponse = customerService.deleteCustomerById(id);
            return ResponseEntity.ok(deleteResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
