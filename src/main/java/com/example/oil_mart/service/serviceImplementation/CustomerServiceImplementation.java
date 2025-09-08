package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.CustomerSaveRequest;
import com.example.oil_mart.dto.request.CustomerUpdateRequest;
import com.example.oil_mart.dto.response.CustomerResponse;
import com.example.oil_mart.dto.response.SalesOrderResponse;
import com.example.oil_mart.model.Customer;
import com.example.oil_mart.model.Sales_Order;
import com.example.oil_mart.repository.CustomerRepository;
import com.example.oil_mart.repository.SalesOrderRepository;
import com.example.oil_mart.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImplementation implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Override
    public CustomerResponse saveCustomer(CustomerSaveRequest saveRequest) {
        try {
            Customer customer = new Customer();
            customer.setCustomer_code(saveRequest.getCustomerCode());
            customer.setCustomer_name(saveRequest.getCustomerName());
            customer.setCustomer_phone(saveRequest.getCustomerPhone());
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
    public List<SalesOrderResponse> getCustomerCreditOrders(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Fetch all orders of this customer
        List<Sales_Order> orders = salesOrderRepository.findByCustomer(customer);

        // Filter only CREDIT orders
        return orders.stream()
                .filter(order -> "CREDIT".equalsIgnoreCase(order.getSalesOrderType()))
                .map(order -> {
                    SalesOrderResponse response = new SalesOrderResponse();
                    response.setId(order.getId());
                    response.setSalesOrderNo(order.getSalesOrderNo()); // use correct field
                    response.setCreatedAt(order.getCreatedAt());
                    response.setTotalAmount(order.getTotalAmount());
                    response.setStatus(order.getStatus());
                    response.setNote(order.getNote());
                    response.setSalesOrderType(order.getSalesOrderType()); // CREDIT / CASH
                    response.setCustomerId(customer.getId());
                    return response;
                })
                .collect(Collectors.toList());
    }



    @Override
    public CustomerResponse updateCustomer(CustomerUpdateRequest updateRequest) {
        Customer customer = customerRepository.findById(updateRequest.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        boolean nameChanged = !customer.getCustomer_name().equals(updateRequest.getCustomerName());
        boolean phoneChanged = !customer.getCustomer_phone().equals(updateRequest.getCustomerPhone());

        // Update fields
        customer.setCustomer_name(updateRequest.getCustomerName());
        customer.setCustomer_phone(updateRequest.getCustomerPhone());

        // ✅ Update code if name or phone changed
        if (nameChanged || phoneChanged) {
            String firstName = updateRequest.getCustomerName().split("\\s+")[0]; // take first word
            String newCode = firstName + "_" + updateRequest.getCustomerPhone();
            customer.setCustomer_code(newCode);
        }

        Customer updatedCustomer = customerRepository.save(customer);
        return returnResponse(updatedCustomer);
    }



    @Override
    public CustomerResponse deleteCustomerById(Long id) {
        try {
            Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

            // ✅ Check if the customer has existing sales orders using repository
            boolean hasOrders = salesOrderRepository.existsByCustomerId(id);

            if (hasOrders) {
                throw new RuntimeException("Cannot delete customer. Customer has associated sales orders.");
            }

            customerRepository.delete(customer);
            return returnResponse(customer);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting customer: " + e.getMessage(), e);
        }
    }



    private CustomerResponse returnResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setCustomerCode(customer.getCustomer_code());
        response.setCustomerName(customer.getCustomer_name());
        response.setCustomerPhone(customer.getCustomer_phone());
        response.setCreatedAt(customer.getCreatedAt());
        return response;
    }
}