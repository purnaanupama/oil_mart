package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.SupplierItemSaveRequest;
import com.example.oil_mart.dto.response.ItemResponse;
import com.example.oil_mart.dto.response.ItemResponseForSupplier;
import com.example.oil_mart.dto.response.SupplierItemResponse;
import com.example.oil_mart.dto.response.SupplierWithItemsResponse;
import com.example.oil_mart.model.Item;
import com.example.oil_mart.model.Supplier;
import com.example.oil_mart.model.SupplierItem;
import com.example.oil_mart.repository.ItemRepository;
import com.example.oil_mart.repository.SupplierItemRepository;
import com.example.oil_mart.repository.SupplierRepository;
import com.example.oil_mart.service.SupplierItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierItemServiceImplementation implements SupplierItemService {

    private final SupplierItemRepository supplierItemRepository;
    private final SupplierRepository supplierRepository;
    private final ItemRepository itemRepository;

    public SupplierItemServiceImplementation(SupplierItemRepository supplierItemRepository,
                                   SupplierRepository supplierRepository,
                                   ItemRepository itemRepository) {
        this.supplierItemRepository = supplierItemRepository;
        this.supplierRepository = supplierRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public SupplierItemResponse save(SupplierItemSaveRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        SupplierItem supplierItem = new SupplierItem();
        supplierItem.setSupplier(supplier);
        supplierItem.setItem(item);

        SupplierItem saved = supplierItemRepository.save(supplierItem);

        return mapToResponse(saved);
    }

    @Override
    public List<SupplierItemResponse> getAll() {
        return supplierItemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierItemResponse> getBySupplier(Long supplierId) {
        return supplierItemRepository.findBySupplier_Id(supplierId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierItemResponse> getByItem(Long itemId) {
        return supplierItemRepository.findByItem_Id(itemId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        supplierItemRepository.deleteById(id);
    }

    @Override
    public SupplierWithItemsResponse getSupplierWithItems(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        List<SupplierItem> supplierItems = supplierItemRepository.findBySupplier_Id(supplierId);

        List<ItemResponseForSupplier> itemResponses = supplierItems.stream()
                .map(si -> new ItemResponseForSupplier(
                        si.getItem().getId().longValue(),
                        si.getItem().getItemCode(),
                        si.getItem().getItemDescription(),
                        si.getItem().getWholesalePrice(),
                        si.getItem().getRetailPrice()
                ))
                .toList();

        return new SupplierWithItemsResponse(
                supplier.getId(),
                supplier.getSupplierName(),
                supplier.getSupplierPhone(),
                supplier.getSupplierAddress(),
                supplier.getCreatedAt(),
                itemResponses
        );
    }

    private SupplierItemResponse mapToResponse(SupplierItem entity) {
        SupplierItemResponse response = new SupplierItemResponse();
        response.setId(entity.getId());
        response.setSupplierId(entity.getSupplier().getId());
        response.setSupplierName(entity.getSupplier().getSupplierName());
        response.setItemId(Long.valueOf(entity.getItem().getId()));
        response.setItemCode(entity.getItem().getItemCode());
        response.setItemDescription(entity.getItem().getItemDescription());
        return response;
    }
}
