package com.example.oil_mart.service;

import com.example.oil_mart.dto.request.CategorySaveRequest;
import com.example.oil_mart.dto.request.CategoryUpdateRequest;
import com.example.oil_mart.dto.response.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    CategoryResponse save(CategorySaveRequest saveRequest);

    List<CategoryResponse> getAll();

    CategoryResponse getById(Integer id);

    CategoryResponse update(CategoryUpdateRequest updateRequest);

}
