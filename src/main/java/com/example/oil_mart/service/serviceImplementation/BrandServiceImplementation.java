package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.BrandSaveRequest;
import com.example.oil_mart.dto.request.BrandUpdateRequest;
import com.example.oil_mart.dto.response.BrandResponse;
import com.example.oil_mart.enums.Status;
import com.example.oil_mart.model.Brand;
import com.example.oil_mart.repository.BrandRepository;
import com.example.oil_mart.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandServiceImplementation implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public BrandResponse save(BrandSaveRequest saveRequest) {
        try {
            Brand brand = new Brand();
            brand.setBrandName(saveRequest.getBrandName());
            brand.setCreatedBy(saveRequest.getCreatedBy());
            brand.setStatus(Status.ACTIVE);

            Brand savedBrand = brandRepository.save(brand);
            return returnResponse(savedBrand);
        } catch (Exception e) {
            throw new RuntimeException("Error saving brand", e);
        }
    }

    @Override
    public List<BrandResponse> getAll() {
        return brandRepository.findAll()
                .stream()
                .map(BrandServiceImplementation::returnResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BrandResponse getById(Integer id) {
        return brandRepository.findById(id)
                .map(BrandServiceImplementation::returnResponse)
                .orElse(null);
    }

    @Override
    public BrandResponse update(BrandUpdateRequest updateRequest) {
        Brand brand = brandRepository.findById(updateRequest.getId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        brand.setBrandName(updateRequest.getBrandName());
        brand.setModifiedBy(updateRequest.getModifiedBy());
        brand.setStatus(updateRequest.getStatus());

        Brand updated = brandRepository.save(brand);
        return returnResponse(updated);
    }

    private static BrandResponse returnResponse(Brand brand) {
        BrandResponse response = new BrandResponse();
        response.setId(brand.getId());
        response.setBrandName(brand.getBrandName());
        response.setCreatedBy(brand.getCreatedBy());
        response.setCreatedDateTime(brand.getCreatedDateTime());
        return response;
    }
}
