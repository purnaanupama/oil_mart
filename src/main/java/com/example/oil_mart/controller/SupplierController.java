package com.example.oil_mart.controller;

import com.example.oil_mart.dto.request.SupplierSaveRequest;
import com.example.oil_mart.dto.request.SupplierUpdateRequest;
import com.example.oil_mart.dto.response.SupplierResponse;
import com.example.oil_mart.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public SupplierResponse createSupplier(@RequestBody SupplierSaveRequest saveRequest) {
        return supplierService.save(saveRequest);
    }

    @GetMapping
    public List<SupplierResponse> getAll() {
        return supplierService.getAll();
    }

    @GetMapping("/{id}")
    public SupplierResponse getById(@PathVariable Long id) {
        return supplierService.getById(id);
    }

    @PutMapping
    public SupplierResponse update(@RequestBody SupplierUpdateRequest updateRequest) {
        return supplierService.update(updateRequest);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        supplierService.delete(id);
    }
}
