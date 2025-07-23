package com.example.oil_mart.controller;

import com.example.oil_mart.dto.request.CategorySaveRequest;
import com.example.oil_mart.dto.request.CategoryUpdateRequest;
import com.example.oil_mart.dto.response.CategoryResponse;
import com.example.oil_mart.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping()
    public ResponseEntity<CategoryResponse> saveCategory(@RequestBody CategorySaveRequest saveRequest) {
        try {
            CategoryResponse saveResponse = categoryService.save(saveRequest);

            return ResponseEntity.ok(saveResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping()
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        try {
            List<CategoryResponse> getAllResponse = categoryService.getAll();

            return ResponseEntity.ok(getAllResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{categoryId}")
    public ResponseEntity<CategoryResponse> getByCategoryId(@PathVariable("categoryId") Integer id) {
        try {
            CategoryResponse getByIdResponse = categoryService.getById(id);

            return ResponseEntity.ok(getByIdResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping()
    public ResponseEntity<CategoryResponse> updateCategory(@RequestBody CategoryUpdateRequest updateRequest) {
        try {
            CategoryResponse updateResponse = categoryService.update(updateRequest);

            return ResponseEntity.ok(updateResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
