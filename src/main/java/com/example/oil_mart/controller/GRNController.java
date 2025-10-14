package com.example.oil_mart.controller;

import com.example.oil_mart.dto.request.GRNSaveRequest;
import com.example.oil_mart.dto.response.GRNResponse;
import com.example.oil_mart.dto.response.PageResponse;
import com.example.oil_mart.service.GRNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grn")
public class GRNController {

    @Autowired
    private GRNService grnService;

    @PostMapping
    public ResponseEntity<GRNResponse> createGRN(@RequestBody GRNSaveRequest request) {
        GRNResponse response = grnService.createGRN(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/last-number")
    public ResponseEntity<String> getLastGrnNumber() {
        String lastGrnNumber = grnService.getLastGrnNumber();
        return ResponseEntity.ok(lastGrnNumber);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGRN(@PathVariable Long id) {
        System.out.println("id"+id);
        grnService.deleteGRN(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GRNResponse>> getAllGRNs() {
        List<GRNResponse> grnResponses = grnService.getAllGRNs();
        return ResponseEntity.ok(grnResponses);
    }

    // New paginated endpoint
    @GetMapping("/paginated")
    public ResponseEntity<PageResponse<GRNResponse>> getAllGRNsPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "grnId") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        PageResponse<GRNResponse> response = grnService.getAll(page, size, sortBy, sortDir);
        return ResponseEntity.ok(response);
    }
}