package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.BrandSaveRequest;
import com.example.oil_mart.dto.request.BrandUpdateRequest;
import com.example.oil_mart.dto.response.BrandResponse;
import com.example.oil_mart.enums.Status;
import com.example.oil_mart.model.Brand;
import com.example.oil_mart.repository.BrandRepository;
import com.example.oil_mart.service.BrandService;

import java.util.List;
import java.util.stream.Collectors;

public class BrandServiceImplementation implements BrandService {

    private BrandRepository brandRepository;

    @Override
    public BrandResponse save(BrandSaveRequest saveRequest) {
        try {
            Brand brand = new Brand();

            brand.setBrandName(saveRequest.getBrandName());
            brand.setCreatedBy(saveRequest.getCreatedBy());
            brand.setModifiedBy(saveRequest.getCreatedBy());
            brand.setStatus(Status.ACTIVE);

            Brand saveResponse = brandRepository.save(brand);

            return returnResponse(saveResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<BrandResponse> getAll() {
        try {
            return brandRepository.findAll().stream().map(BrandServiceImplementation::returnResponse).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BrandResponse getById(Integer id) {
        try {
            return brandRepository.findById(id).map(BrandServiceImplementation::returnResponse).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BrandResponse update(BrandUpdateRequest updateRequest) {
        return null;
    }

    private static BrandResponse returnResponse(Brand brand) {
        BrandResponse response = new BrandResponse();

        response.setId(brand.getId());
        response.setBrandName(brand.getBrandName());
        response.setCreatedBy(brand.getCreatedBy());
        response.setCreatedDateTime(brand.getCreatedDateTime());
        response.setModifiedBy(brand.getModifiedBy());
        response.setModifiedDateTime(brand.getModifiedDateTime());
        response.setStatus(brand.getStatus());

        return response;
    }
}
