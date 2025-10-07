package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.SupplierSaveRequest;
import com.example.oil_mart.dto.request.SupplierUpdateRequest;
import com.example.oil_mart.dto.response.SupplierResponse;
import com.example.oil_mart.model.Supplier;
import com.example.oil_mart.repository.SupplierRepository;
import com.example.oil_mart.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierServiceImplementation implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public SupplierResponse save(SupplierSaveRequest saveRequest) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(saveRequest.getSupplierName());
        supplier.setSupplierPhone(saveRequest.getSupplierPhone());
        supplier.setSupplierAddress(saveRequest.getSupplierAddress());

        Supplier saved = supplierRepository.save(supplier);
        return mapToResponse(saved);
    }

    @Override
    public List<SupplierResponse> getAll() {
        return supplierRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierResponse getById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        return mapToResponse(supplier);
    }

    @Override
    public SupplierResponse update(SupplierUpdateRequest updateRequest) {
        Supplier supplier = supplierRepository.findById(updateRequest.getId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + updateRequest.getId()));

        supplier.setSupplierName(updateRequest.getSupplierName());
        supplier.setSupplierPhone(updateRequest.getSupplierPhone());
        supplier.setSupplierAddress(updateRequest.getSupplierAddress());

        Supplier updated = supplierRepository.save(supplier);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        return new SupplierResponse(
                supplier.getId(),
                supplier.getSupplierName(),
                supplier.getSupplierPhone(),
                supplier.getSupplierAddress(),
                supplier.getCreatedAt()
        );
    }
}
