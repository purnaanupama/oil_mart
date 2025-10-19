package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.GRNItemSaveRequest;
import com.example.oil_mart.dto.request.GRNSaveRequest;
import com.example.oil_mart.dto.response.GRNItemResponse;
import com.example.oil_mart.dto.response.GRNResponse;
import com.example.oil_mart.model.Grn;
import com.example.oil_mart.model.GrnItem;
import com.example.oil_mart.model.Item;
import com.example.oil_mart.model.Supplier;
import com.example.oil_mart.repository.GRNRepository;
import com.example.oil_mart.repository.ItemRepository;
import com.example.oil_mart.repository.SupplierRepository;
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

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    @Transactional
    public GRNResponse createGRN(GRNSaveRequest request) {
        // Create GRN entity
        Grn grn = new Grn();
        grn.setGrnNumber(request.getGrnNumber());
        grn.setInvoiceNumber(request.getInvoiceNumber());
        grn.setTotalAmount(request.getTotalAmount());
        grn.setCreatedAt(parseDateTime(request.getCreatedAt()));

        // Create GRN items
        List<GrnItem> grnItems = new ArrayList<>();
        for (GRNItemSaveRequest itemRequest : request.getItems()) {
            GrnItem grnItem = new GrnItem();
            grnItem.setItemId(itemRequest.getItemId());
            grnItem.setSupplierId(itemRequest.getSupplierId());
            grnItem.setQuantity(itemRequest.getQuantity());
            grnItem.setUnitPrice(itemRequest.getUnitPrice());
            grnItem.setTotalAmount(itemRequest.getTotalPrice());
            grnItem.setCreatedAt(parseDateTime(itemRequest.getCreatedAt()));
            grnItem.setGrn(grn);

            // Fetch item details
            Item stockItem = itemRepository.findById(Math.toIntExact(itemRequest.getItemId()))
                    .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemRequest.getItemId()));
            grnItem.setItemCode(stockItem.getItemCode());

            // Fetch supplier details
            Supplier supplier = supplierRepository.findById(itemRequest.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + itemRequest.getSupplierId()));
            grnItem.setSupplierName(supplier.getSupplierName());

            // --- STOCK MANAGEMENT: Update item stock ---
            // Parse pack size safely
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

            // Update stock values
            stockItem.setAvailableStock(stockItem.getAvailableStock() + totalQty);
            stockItem.setStockInLiters(stockItem.getStockInLiters() + litersToAdd);
            stockItem.setStockInMillilitres(stockItem.getStockInMillilitres() + mlToAdd);

            itemRepository.save(stockItem);
            grnItems.add(grnItem);
        }

        grn.setItems(grnItems);
        Grn savedGrn = grnRepository.save(grn);

        return mapToResponse(savedGrn);
    }

    @Override
    public String getLastGrnNumber() {
        Grn lastGrn = grnRepository.findTopByOrderByGrnIdDesc();
        return lastGrn != null ? lastGrn.getGrnNumber() : "PO-2025-000";
    }

    @Override
    @Transactional
    public void deleteGRN(Long id) {
        Grn grn = grnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GRN not found with ID: " + id));

        // --- STOCK REVERSAL: Subtract stock when GRN is deleted ---
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
                packSize = 0;
            }

            String packUnit = stockItem.getPackUnit() != null ? stockItem.getPackUnit().toLowerCase() : "";

            if ("l".equals(packUnit)) {
                double litersToSubtract = item.getQuantity() * packSize;
                double mlToSubtract = litersToSubtract * 1000;
                stockItem.setStockInLiters(stockItem.getStockInLiters() - litersToSubtract);
                stockItem.setStockInMillilitres(stockItem.getStockInMillilitres() - mlToSubtract);
            } else if ("ml".equals(packUnit)) {
                double mlToSubtract = item.getQuantity() * packSize;
                double litersToSubtract = mlToSubtract / 1000.0;
                stockItem.setStockInLiters(stockItem.getStockInLiters() - litersToSubtract);
                stockItem.setStockInMillilitres(stockItem.getStockInMillilitres() - mlToSubtract);
            }

            itemRepository.save(stockItem);
        }

        // Clear GRN items to maintain DB integrity
        grn.getItems().clear();

        // Delete the GRN
        grnRepository.delete(grn);
    }

    @Override
    public List<GRNResponse> getAllGRNs() {
        return grnRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GRNResponse mapToResponse(Grn grn) {
        GRNResponse response = new GRNResponse();
        response.setId(grn.getGrnId().intValue());
        response.setGrnNumber(grn.getGrnNumber());
        response.setInvoiceNumber(grn.getInvoiceNumber());
        response.setTotalAmount(grn.getTotalAmount());
        response.setCreatedAt(formatDateTime(grn.getCreatedAt()));

        List<GRNItemResponse> itemResponses = grn.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        response.setItems(itemResponses);
        return response;
    }

    private GRNItemResponse mapItemToResponse(GrnItem item) {
        GRNItemResponse response = new GRNItemResponse();
        response.setId(item.getId());
        response.setItemId(item.getItemId());
        response.setSupplierId(item.getSupplierId());
        response.setItemCode(item.getItemCode());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setTotalAmount(item.getTotalAmount());
        response.setSupplierName(item.getSupplierName());
        response.setCreatedAt(formatDateTime(item.getCreatedAt()));
        return response;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
            return LocalDateTime.parse(dateTimeStr, isoFormatter);
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return dateTime.format(formatter);
    }
}