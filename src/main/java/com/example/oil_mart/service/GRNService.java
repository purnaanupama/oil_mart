package com.example.oil_mart.service;

import com.example.oil_mart.dto.request.GRNSaveRequest;
import com.example.oil_mart.dto.response.GRNResponse;

import java.util.List;

public interface GRNService {
    GRNResponse createGRN(GRNSaveRequest request);
    String getLastGrnNumber();
    void deleteGRN(Long id);
    List<GRNResponse> getAllGRNs();
}