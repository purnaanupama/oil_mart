package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.GRNSaveRequest;
import com.example.oil_mart.dto.request.GRNItemSaveRequest;
import com.example.oil_mart.dto.response.GRNResponse;
import com.example.oil_mart.dto.response.GRNItemResponse;
import com.example.oil_mart.model.Grn;
import com.example.oil_mart.model.GrnItem;
import com.example.oil_mart.repository.GRNRepository;
import com.example.oil_mart.service.GRNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GRNServiceImplementation implements GRNService {

    @Autowired
    private GRNRepository grnRepository;

    @Override
    @Transactional
    public GRNResponse createGRN(GRNSaveRequest request) {
        Grn grn = new Grn();
        grn.setGrnNumber(request.getGrnNumber());
        grn.setTotalAmount(request.getTotalAmount());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime createdAt = request.getCreatedAt() == null ?
                LocalDateTime.now() :
                LocalDateTime.parse(request.getCreatedAt(), formatter);        grn.setCreatedAt(createdAt);

        List<GrnItem> items = new ArrayList<>();

        for (GRNItemSaveRequest itemRequest : request.getItems()) {
            GrnItem item = new GrnItem();
            item.setItemId(itemRequest.getItemId());
            // Convert double to Integer if necessary
            item.setQuantity((int) itemRequest.getQuantity());
            // Correct method names based on likely DTO structure
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setTotalAmount(itemRequest.getTotalPrice());
            item.setSupplierName(itemRequest.getSupplier_name());
            item.setCreatedAt(createdAt);
            item.setGrn(grn);
            items.add(item);
        }

        grn.setItems(items);
        Grn savedGrn = grnRepository.save(grn);

        return convertToResponse(savedGrn);
    }

    @Override
    public String getLastGrnNumber() {
        Grn lastGrn = grnRepository.findTopByOrderByGrnIdDesc();
        return lastGrn != null ? lastGrn.getGrnNumber() : "GRN-2025-00000";
    }

    private GRNResponse convertToResponse(Grn grn) {
        GRNResponse response = new GRNResponse();
        response.setId(grn.getGrnId().intValue());
        response.setGrnNumber(grn.getGrnNumber());
        response.setTotalAmount(grn.getTotalAmount());
        response.setCreatedAt(grn.getCreatedAt().toString());

        List<GRNItemResponse> itemResponses = grn.getItems().stream().map(item -> {
            GRNItemResponse itemResponse = new GRNItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setItemId(item.getItemId());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setTotalAmount(item.getTotalAmount());
            itemResponse.setSupplierName(item.getSupplierName());
            itemResponse.setCreatedAt(item.getCreatedAt().toString());
            return itemResponse;
        }).collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }

    @Override
    @Transactional
    public void deleteGRN(Long id) {
        Grn grn = grnRepository.findById(id).orElseThrow(() -> new RuntimeException("GRN not found"));

        // Delete all grn_items associated with the grn_id
        grn.getItems().clear(); // Ensures the relationship is cleared if cascade is not configured

        grnRepository.delete(grn); // Deletes the GRN
    }

    //method for get all GRNs
    @Override
    public List<GRNResponse> getAllGRNs() {
        List<Grn> grns = grnRepository.findAll();
        return grns.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

}