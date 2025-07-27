package com.example.oil_mart.controller;

import com.example.oil_mart.dto.request.BrandSaveRequest;
import com.example.oil_mart.dto.request.BrandUpdateRequest;
import com.example.oil_mart.dto.response.BrandResponse;
import com.example.oil_mart.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;
    @PostMapping()
    public ResponseEntity<BrandResponse> saveBrand(@RequestBody BrandSaveRequest saveRequest) {
        try {
            BrandResponse saveResponse = brandService.save(saveRequest);
            return ResponseEntity.ok(saveResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping()
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        try {
            List<BrandResponse> getAllResponse = brandService.getAll();

            return ResponseEntity.ok(getAllResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{brandId}")
    public ResponseEntity<BrandResponse> getByBrandId(@PathVariable("brandId") Integer id) {
        try {
            BrandResponse getByIdResponse = brandService.getById(id);

            return ResponseEntity.ok(getByIdResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping()
    public ResponseEntity<BrandResponse> updateBrand(@RequestBody BrandUpdateRequest updateRequest) {
        try {
            BrandResponse updateResponse = brandService.update(updateRequest);

            return ResponseEntity.ok(updateResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("{brandId}")
    public ResponseEntity<?> deleteBrand(@PathVariable("brandId") Integer brandId) {
        try {
            BrandResponse deletedBrand = brandService.deleteById(brandId);

            if (deletedBrand != null) {
                return ResponseEntity.ok(deletedBrand);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



}
