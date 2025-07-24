package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.CategorySaveRequest;
import com.example.oil_mart.dto.request.CategoryUpdateRequest;
import com.example.oil_mart.dto.response.CategoryResponse;
import com.example.oil_mart.enums.Status;
import com.example.oil_mart.model.Category;
import com.example.oil_mart.repository.CategoryRepository;
import com.example.oil_mart.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImplementation implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryResponse save(CategorySaveRequest saveRequest) {
        try {
            Category category = new Category();

            category.setCategoryName(saveRequest.getCategoryName());
            category.setCreatedBy(saveRequest.getCreatedBy());
            category.setModifiedBy(saveRequest.getCreatedBy());
            category.setStatus(Status.ACTIVE);

            Category saveResponse = categoryRepository.save(category);

            return returnResponse(saveResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryResponse> getAll() {
        try {
            return categoryRepository.findAll().stream().map(CategoryServiceImplementation::returnResponse).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryResponse getById(Integer id) {
        try {
            return categoryRepository.findById(id).map(CategoryServiceImplementation::returnResponse).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryResponse update(CategoryUpdateRequest updateRequest) {
        try {
            Category updateResponse = new Category();

            Category category = categoryRepository.getById(updateRequest.getId());

            if (category != null) {
                category.setCategoryName(updateRequest.getCategoryName());
                category.setStatus(updateRequest.getStatus());
                category.setModifiedBy(updateRequest.getModifiedBy());

                updateResponse = categoryRepository.save(category);
            }

            return returnResponse(updateResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static CategoryResponse returnResponse(Category category) {
        CategoryResponse response = new CategoryResponse();

        response.setId(category.getId());
        response.setCategoryName(category.getCategoryName());
        response.setCreatedBy(category.getCreatedBy());
        response.setCreatedDateTime(category.getCreatedDateTime());
        response.setModifiedBy(category.getModifiedBy());
        response.setModifiedDateTime(category.getModifiedDateTime());
        response.setStatus(category.getStatus());

        return response;
    }
}
