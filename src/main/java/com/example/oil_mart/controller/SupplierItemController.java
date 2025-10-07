package com.example.oil_mart.controller;

import com.example.oil_mart.dto.request.SupplierItemSaveRequest;
import com.example.oil_mart.dto.response.SupplierItemResponse;
import com.example.oil_mart.service.SupplierItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier-items")
public class SupplierItemController {

    private final SupplierItemService supplierItemService;

    public SupplierItemController(SupplierItemService supplierItemService) {
        this.supplierItemService = supplierItemService;
    }

    @PostMapping
    public ResponseEntity<SupplierItemResponse> create(@RequestBody SupplierItemSaveRequest request) {
        return ResponseEntity.ok(supplierItemService.save(request));
    }

    @GetMapping
    public ResponseEntity<List<SupplierItemResponse>> getAll() {
        return ResponseEntity.ok(supplierItemService.getAll());
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<SupplierItemResponse>> getBySupplier(@PathVariable Long supplierId) {
        return ResponseEntity.ok(supplierItemService.getBySupplier(supplierId));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<SupplierItemResponse>> getByItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(supplierItemService.getByItem(itemId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supplierItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
