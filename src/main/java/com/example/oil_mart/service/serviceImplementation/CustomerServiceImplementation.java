package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.CustomerSaveRequest;
import com.example.oil_mart.dto.request.CustomerUpdateRequest;
import com.example.oil_mart.dto.response.CustomerResponse;
import com.example.oil_mart.model.Customer;
import com.example.oil_mart.repository.CustomerRepository;
import com.example.oil_mart.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImplementation implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CustomerResponse saveCustomer(CustomerSaveRequest saveRequest) {
        try {
            Customer customer = new Customer();
            customer.setCustomerNo(saveRequest.getCustomerNo());
            customer.setCustomer_name(saveRequest.getCustomerName());
            customer.setCustomer_address(saveRequest.getCustomerAddress());
            customer.setCustomer_phone(saveRequest.getCustomerPhone());
            customer.setCustomer_nic(saveRequest.getCustomerNic());
            customer.setCreatedAt(saveRequest.getCreatedAt());

            Customer savedCustomer = customerRepository.save(customer);
            return returnResponse(savedCustomer);
        } catch (Exception e) {
            throw new RuntimeException("Error saving customer", e);
        }
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::returnResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::returnResponse)
                .orElse(null);
    }

    @Override
    public CustomerResponse updateCustomer(CustomerUpdateRequest updateRequest) {
        Customer customer = customerRepository.findById(updateRequest.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setCustomerNo(updateRequest.getCustomerNo());
        customer.setCustomer_name(updateRequest.getCustomerName());
        customer.setCustomer_address(updateRequest.getCustomerAddress());
        customer.setCustomer_phone(updateRequest.getCustomerPhone());
        customer.setCustomer_nic(updateRequest.getCustomerNic());
        customer.setCreatedAt(updateRequest.getCreatedAt());

        Customer updatedCustomer = customerRepository.save(customer);
        return returnResponse(updatedCustomer);
    }

    @Override
    public CustomerResponse deleteCustomerById(Long id) {
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
            customerRepository.delete(customer);
            return returnResponse(customer);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }

    private CustomerResponse returnResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setCustomerNo(customer.getCustomerNo());
        response.setCustomerName(customer.getCustomer_name());
        response.setCustomerAddress(customer.getCustomer_address());
        response.setCustomerPhone(customer.getCustomer_phone());
        response.setCustomerNic(customer.getCustomer_nic());
        response.setCreatedAt(customer.getCreatedAt());
        return response;
    }
}