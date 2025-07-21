package com.example.oil_mart.service;

import com.example.oil_mart.dto.request.BrandSaveRequest;
import com.example.oil_mart.dto.request.BrandUpdateRequest;
import com.example.oil_mart.dto.response.BrandResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BrandService {

    BrandResponse save(BrandSaveRequest saveRequest);

    List<BrandResponse> getAll();

    BrandResponse getById(Integer id);

    BrandResponse update(BrandUpdateRequest updateRequest);

}
