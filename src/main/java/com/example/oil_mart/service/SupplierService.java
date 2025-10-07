package com.example.oil_mart.service;

import com.example.oil_mart.dto.request.SupplierSaveRequest;
import com.example.oil_mart.dto.request.SupplierUpdateRequest;
import com.example.oil_mart.dto.response.SupplierResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SupplierService {

    SupplierResponse save(SupplierSaveRequest saveRequest);

    List<SupplierResponse> getAll();

    SupplierResponse getById(Long id);

    SupplierResponse update(SupplierUpdateRequest updateRequest);

    void delete(Long id);
}
