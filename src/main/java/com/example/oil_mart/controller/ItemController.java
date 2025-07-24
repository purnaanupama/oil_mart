package com.example.oil_mart.controller;

import com.example.oil_mart.dto.request.ItemSaveRequest;
import com.example.oil_mart.dto.request.ItemUpdateRequest;
import com.example.oil_mart.dto.response.ItemResponse;
import com.example.oil_mart.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping()
    public ResponseEntity<ItemResponse> saveItem(@RequestBody ItemSaveRequest saveRequest) {
        try {
            ItemResponse saveResponse = itemService.save(saveRequest);

            return ResponseEntity.ok(saveResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping()
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        try {
            List<ItemResponse> getAllResponse = itemService.getAll();

            return ResponseEntity.ok(getAllResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{itemId}")
    public ResponseEntity<ItemResponse> getByItemId(@PathVariable("itemId") Integer id) {
        try {
            ItemResponse getByIdResponse = itemService.getById(id);

            return ResponseEntity.ok(getByIdResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping()
    public ResponseEntity<ItemResponse> updateItem(@RequestBody ItemUpdateRequest updateRequest) {
        try {
            ItemResponse updateResponse = itemService.update(updateRequest);

            return ResponseEntity.ok(updateResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
