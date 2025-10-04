package com.example.oil_mart.service;

import com.example.oil_mart.dto.request.SupplierItemSaveRequest;
import com.example.oil_mart.dto.response.SupplierItemResponse;
import com.example.oil_mart.dto.response.SupplierWithItemsResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SupplierItemService {

    SupplierItemResponse save(SupplierItemSaveRequest request);

    List<SupplierItemResponse> getAll();

    List<SupplierItemResponse> getBySupplier(Long supplierId);

    List<SupplierItemResponse> getByItem(Long itemId);

    void delete(Long id);

    SupplierWithItemsResponse getSupplierWithItems(Long supplierId);
}
