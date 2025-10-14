package com.example.oil_mart.service;

import com.example.oil_mart.dto.request.GRNSaveRequest;
import com.example.oil_mart.dto.response.GRNResponse;
import com.example.oil_mart.dto.response.ItemResponse;
import com.example.oil_mart.dto.response.PageResponse;

import java.util.List;

public interface GRNService {
    GRNResponse createGRN(GRNSaveRequest request);
    String getLastGrnNumber();
    void deleteGRN(Long id);
    List<GRNResponse> getAllGRNs();
    PageResponse<GRNResponse> getAll(int page, int size, String sortBy, String sortDir);

}