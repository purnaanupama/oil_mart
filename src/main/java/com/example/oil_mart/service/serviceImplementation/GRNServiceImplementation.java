package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.GRNSaveRequest;
import com.example.oil_mart.dto.request.GRNItemSaveRequest;
import com.example.oil_mart.dto.response.GRNResponse;
import com.example.oil_mart.dto.response.GRNItemResponse;
import com.example.oil_mart.dto.response.ItemResponse;
import com.example.oil_mart.dto.response.PageResponse;
import com.example.oil_mart.model.Grn;
import com.example.oil_mart.model.GrnItem;
import com.example.oil_mart.model.Item;
import com.example.oil_mart.repository.GRNRepository;
import com.example.oil_mart.repository.ItemRepository;
import com.example.oil_mart.service.GRNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @Autowired
    private ItemRepository itemRepository;
    @Override
    @Transactional
    public GRNResponse createGRN(GRNSaveRequest request) {
        Grn grn = new Grn();
        grn.setGrnNumber(request.getGrnNumber());
        grn.setTotalAmount(request.getTotalAmount());
        grn.setInvoiceNumber(request.getInvoiceNumber());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime createdAt = request.getCreatedAt() == null ?
                LocalDateTime.now() :
                LocalDateTime.parse(request.getCreatedAt(), formatter);
                grn.setCreatedAt(createdAt);

        List<GrnItem> items = new ArrayList<>();

        for (GRNItemSaveRequest itemRequest : request.getItems()) {
            GrnItem item = new GrnItem();
            item.setItemId(itemRequest.getItemId());
            item.setQuantity((int) itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setTotalAmount(itemRequest.getTotalPrice());
            item.setSupplierName(itemRequest.getSupplier_name());
            item.setCreatedAt(createdAt);
            item.setGrn(grn);

            Item stockItem = itemRepository.findById(Math.toIntExact(item.getItemId()))
                    .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemRequest.getItemId()));

            // --- Parse pack size safely ---
            double packSizeValue = 0.0;
            try {
                packSizeValue = Double.parseDouble(stockItem.getPackSize());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid pack size for item: " + stockItem.getItemCode());
            }

            double totalQty = itemRequest.getQuantity(); // number of packs
            double litersToAdd = 0.0;
            double mlToAdd = 0.0;

            if (stockItem.getPackUnit().equalsIgnoreCase("l")) {
                // Example: 2 packs × 5L = 10L → store in liters & milliliters
                litersToAdd = totalQty * packSizeValue;
                mlToAdd = litersToAdd * 1000;
            } else if (stockItem.getPackUnit().equalsIgnoreCase("ml")) {
                // Example: 2 packs × 500ml = 1000ml = 1.0L
                mlToAdd = totalQty * packSizeValue;
                litersToAdd = mlToAdd / 1000.0;
            }

            // --- Update stock values ---
            stockItem.setAvailableStock(stockItem.getAvailableStock() + totalQty);
            stockItem.setStockInLiters(stockItem.getStockInLiters() + litersToAdd);
            stockItem.setStockInMillilitres(stockItem.getStockInMillilitres() + mlToAdd);

            itemRepository.save(stockItem);
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
        response.setInvoiceNumber(grn.getInvoiceNumber());

        List<GRNItemResponse> itemResponses = grn.getItems().stream().map(item -> {
            Item solidItem = itemRepository.findById(item.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with ID: " + item.getItemId()));;
            if (solidItem == null) return null;
            GRNItemResponse itemResponse = new GRNItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setItemId(item.getItemId());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setTotalAmount(item.getTotalAmount());
            itemResponse.setSupplierName(item.getSupplierName());
            itemResponse.setCreatedAt(item.getCreatedAt().toString());

            itemResponse.setItemCode(solidItem.getItemCode());
            return itemResponse;
        }).collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }

    @Override
    @Transactional
    public void deleteGRN(Long id) {
        Grn grn = grnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GRN not found"));

        for (GrnItem item : grn.getItems()) {
            Item stockItem = itemRepository.findById(item.getItemId().intValue())
                    .orElseThrow(() -> new RuntimeException("Item not found with ID: " + item.getItemId()));

            // Subtract the quantity from available stock
            double newStock = stockItem.getAvailableStock() - item.getQuantity();
            stockItem.setAvailableStock(newStock);

            // Adjust stock in liters and millilitres
            double packSize = 0;
            try {
                packSize = Double.parseDouble(stockItem.getPackSize());
            } catch (NumberFormatException e) {
                // If packSize is invalid, treat as 0
            }

            String packUnit = stockItem.getPackUnit().toLowerCase();

            if ("l".equals(packUnit)) {
                stockItem.setStockInLiters(stockItem.getStockInLiters() - (item.getQuantity() * packSize));
                stockItem.setStockInMillilitres(stockItem.getStockInMillilitres() - (item.getQuantity() * packSize * 1000));
            } else if ("ml".equals(packUnit)) {
                double liters = packSize / 1000.0;
                stockItem.setStockInLiters(stockItem.getStockInLiters() - (item.getQuantity() * liters));
                stockItem.setStockInMillilitres(stockItem.getStockInMillilitres() - (item.getQuantity() * packSize));
            } else {
                // Treat as unit (no liters/millilitres)
            }

            itemRepository.save(stockItem);
        }

        // Clear GRN items to maintain DB integrity
        grn.getItems().clear();

        // Delete the GRN
        grnRepository.delete(grn);
    }


    //method for get all GRNs
    @Override
    public List<GRNResponse> getAllGRNs() {
        List<Grn> grns = grnRepository.findAll();
        return grns.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public PageResponse<GRNResponse> getAll(int page, int size, String sortBy, String sortDir) {
        try {
            // Spring Data uses 0-based; we convert 1-based -> 0-based
            int pageIndex = Math.max(page - 1, 0);
            Sort sort = "ASC".equalsIgnoreCase(sortDir)
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(pageIndex, size, sort);
            Page<Grn> pageData = grnRepository.findAll(pageable);

            // Convert Grn entities to GRNResponse DTOs
            List<GRNResponse> content = pageData.getContent()
                    .stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return new PageResponse<>(
                    content,
                    pageIndex + 1,                 // back to 1-based
                    size,
                    pageData.getTotalElements(),
                    pageData.getTotalPages(),
                    pageData.isLast()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error fetching paginated GRNs: " + e.getMessage(), e);
        }
    }

   

}