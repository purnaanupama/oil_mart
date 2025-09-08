package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.ReturnItemReq;
import com.example.oil_mart.dto.request.ReturnReq;
import com.example.oil_mart.dto.response.ReturnItemRes;
import com.example.oil_mart.dto.response.ReturnRes;
import com.example.oil_mart.model.Item;
import com.example.oil_mart.model.Return;
import com.example.oil_mart.model.Return_item;
import com.example.oil_mart.repository.ItemRepository;
import com.example.oil_mart.repository.ReturnItemRepository;
import com.example.oil_mart.repository.ReturnRepository;
import com.example.oil_mart.service.ReturnService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReturnServiceImplementation implements ReturnService {

    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private ReturnItemRepository returnItemRepository;

    @Autowired
    private ItemRepository itemRepository;


    @Override
    @Transactional
    public ReturnRes saveReturn(ReturnReq request) {
        // Validate RONumber
        if (request.getRONumber() == null || request.getRONumber().isEmpty()) {
            request.setRONumber("RO-" + System.currentTimeMillis());
        }

        // 1️⃣ Create and save Return Header
        Return returnRecord = new Return();
        returnRecord.setRONumber(request.getRONumber());
        returnRecord.setCreatedAt(request.getCreatedAt());
        returnRecord = returnRepository.save(returnRecord);

        // 2️⃣ Process Return Items
        List<ReturnItemRes> returnItemResponses = new ArrayList<>();
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (ReturnItemReq itemReq : request.getItems()) {
                Return_item returnItem = new Return_item();
                returnItem.setReturnRecord(returnRecord);
                returnItem.setIsLoose(itemReq.getIsLoose());
                returnItem.setQuantity(itemReq.getQuantity());
                returnItem.setQuantityLitres(itemReq.getQuantityLitres());
                returnItem.setQuantityMiliLitres(itemReq.getQuantityMiliLitres());
                returnItem.setItemId(itemReq.getItemId());

                // Fetch the item
                Item foundItem = itemRepository.findById(returnItem.getItemId())
                        .orElseThrow(() -> new RuntimeException("Item not found with ID: " + returnItem.getItemId()));

                // Deduct stock
                deductItemStock(foundItem, returnItem.getQuantity(), returnItem.getQuantityLitres(), returnItem.getQuantityMiliLitres());
                itemRepository.save(foundItem);

                returnItemRepository.save(returnItem);

                // Add to response
                ReturnItemRes itemRes = new ReturnItemRes();
                itemRes.setId(returnItem.getId());
                itemRes.setItemId(returnItem.getItemId());
                itemRes.setQuantity(returnItem.getQuantity());
                itemRes.setQuantityLitres(returnItem.getQuantityLitres());
                itemRes.setQuantityMiliLitres(returnItem.getQuantityMiliLitres());
                itemRes.setIsLoose(returnItem.getIsLoose());
                returnItemResponses.add(itemRes);
            }
        }

        // 3️⃣ Prepare response
        ReturnRes response = new ReturnRes();
        response.setRONumber(returnRecord.getRONumber());
        response.setCreatedAt(returnRecord.getCreatedAt());
        response.setId(returnRecord.getId());
        response.setItems(returnItemResponses); // Set returned items
        return response;
    }


    // Helper method to get pack size in liters (copied from SalesOrderServiceImplementation)
    private double getPackSizeInLiters(Item item) {
        try {
            if (item.getPackUnit().equalsIgnoreCase("ml")) {
                return Double.parseDouble(item.getPackSize()) / 1000.0;
            } else if (item.getPackUnit().equalsIgnoreCase("L")) {
                return Double.parseDouble(item.getPackSize());
            } else {
                throw new RuntimeException("Unsupported pack unit: " + item.getPackUnit());
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid pack size format for item: " + item.getItemCode());
        }
    }

    private void deductItemStock(Item item, Integer quantity, Double quantityLitres, Double quantityMillilitres) {
        BigDecimal packSizeInLiters = BigDecimal.valueOf(getPackSizeInLiters(item));
        BigDecimal stockInLiters = BigDecimal.valueOf(item.getStockInLiters());

        // Set default values for null quantities
        quantity = (quantity != null) ? quantity : 0;
        quantityLitres = (quantityLitres != null) ? quantityLitres : 0.0;
        quantityMillilitres = (quantityMillilitres != null) ? quantityMillilitres : 0.0;

        // Calculate total liters to deduct
        BigDecimal totalDeductLiters = BigDecimal.ZERO;

        // Deduct based on the number of packs
        if (quantity > 0) {
            totalDeductLiters = totalDeductLiters.add(packSizeInLiters.multiply(BigDecimal.valueOf(quantity)));
        }

        // For loose quantities, use EITHER litres OR millilitres, not both
        if (quantityLitres > 0) {
            totalDeductLiters = totalDeductLiters.add(BigDecimal.valueOf(quantityLitres));
        } else if (quantityMillilitres > 0) {
            // Only use millilitres if litres is 0 or null
            totalDeductLiters = totalDeductLiters.add(BigDecimal.valueOf(quantityMillilitres)
                    .divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP));
        }

        System.out.println("=== STOCK DEDUCTION DEBUG ===");
        System.out.println("Item ID: " + item.getId());
        System.out.println("Item Code: " + item.getItemCode());
        System.out.println("Pack size in liters: " + packSizeInLiters);
        System.out.println("Current stock in liters: " + stockInLiters);
        System.out.println("Total deduct liters: " + totalDeductLiters);
        System.out.println("===============================");

        // Validate stock availability
        if (totalDeductLiters.compareTo(stockInLiters) > 0) {
            throw new RuntimeException("Insufficient stock for item");
        }

        // Deduct stock
        BigDecimal newStockInLiters = stockInLiters.subtract(totalDeductLiters);

        // Update item stock
        item.setStockInLiters(newStockInLiters.setScale(6, RoundingMode.HALF_UP).doubleValue());
        item.setStockInMillilitres(newStockInLiters.multiply(BigDecimal.valueOf(1000))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue());

        // Update available packs
        int newPackQuantity = newStockInLiters.divide(packSizeInLiters, 0, RoundingMode.DOWN).intValue();
        item.setAvailableStock(newPackQuantity);
    }

    @Override
    public List<ReturnRes> getAllReturns() {
        List<Return> returns = returnRepository.findAll();
        List<ReturnRes> responses = new ArrayList<>();

        for (Return returnRecord : returns) {
            ReturnRes response = convertToResponse(returnRecord);
            responses.add(response);
        }

        return responses;
    }

    @Override
    public ReturnRes getReturnById(Long id) {
        Return returnRecord = returnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return not found with ID: " + id));

        return convertToResponse(returnRecord);
    }

    @Override
    @Transactional
    public ReturnRes updateReturn(Long id, ReturnReq request) {
        Return existingReturn = returnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return not found with ID: " + id));

        // Restore stock for existing items before update
        List<Return_item> existingItems = returnItemRepository.findByReturnRecord(existingReturn);
        for (Return_item item : existingItems) {
            Item foundItem = itemRepository.findById(item.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with ID: " + item.getItemId()));
            restoreItemStock(foundItem, item.getQuantity(), item.getQuantityLitres(), item.getQuantityMiliLitres());
            itemRepository.save(foundItem);
        }

        // Delete existing return items
        returnItemRepository.deleteAll(existingItems);

        // Update return record
        existingReturn.setRONumber(request.getRONumber());
        existingReturn.setCreatedAt(request.getCreatedAt());
        existingReturn = returnRepository.save(existingReturn);

        // Process new return items (same logic as saveReturn)
        List<ReturnItemRes> returnItemResponses = new ArrayList<>();
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (ReturnItemReq itemReq : request.getItems()) {
                Return_item returnItem = new Return_item();
                returnItem.setReturnRecord(existingReturn);
                returnItem.setIsLoose(itemReq.getIsLoose());
                returnItem.setQuantity(itemReq.getQuantity());
                returnItem.setQuantityLitres(itemReq.getQuantityLitres());
                returnItem.setQuantityMiliLitres(itemReq.getQuantityMiliLitres());
                returnItem.setItemId(itemReq.getItemId());

                Item foundItem = itemRepository.findById(returnItem.getItemId())
                        .orElseThrow(() -> new RuntimeException("Item not found with ID: " + returnItem.getItemId()));

                deductItemStock(foundItem, returnItem.getQuantity(), returnItem.getQuantityLitres(), returnItem.getQuantityMiliLitres());
                itemRepository.save(foundItem);

                returnItemRepository.save(returnItem);

                ReturnItemRes itemRes = new ReturnItemRes();
                itemRes.setId(returnItem.getId());
                itemRes.setItemId(returnItem.getItemId());
                itemRes.setQuantity(returnItem.getQuantity());
                itemRes.setQuantityLitres(returnItem.getQuantityLitres());
                itemRes.setQuantityMiliLitres(returnItem.getQuantityMiliLitres());
                itemRes.setIsLoose(returnItem.getIsLoose());
                returnItemResponses.add(itemRes);
            }
        }

        ReturnRes response = new ReturnRes();
        response.setRONumber(existingReturn.getRONumber());
        response.setCreatedAt(existingReturn.getCreatedAt());
        response.setId(existingReturn.getId());
        response.setItems(returnItemResponses);
        return response;
    }

    @Override
    @Transactional
    public void deleteReturn(Long id) {
        Return returnRecord = returnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Return not found with ID: " + id));

        // Restore stock for all return items
        List<Return_item> returnItems = returnItemRepository.findByReturnRecord(returnRecord);
        for (Return_item item : returnItems) {
            Item foundItem = itemRepository.findById(item.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with ID: " + item.getItemId()));
            restoreItemStock(foundItem, item.getQuantity(), item.getQuantityLitres(), item.getQuantityMiliLitres());
            itemRepository.save(foundItem);
        }

        // Delete return items first (due to foreign key constraint)
        returnItemRepository.deleteAll(returnItems);

        // Delete return record
        returnRepository.delete(returnRecord);
    }

    private ReturnRes convertToResponse(Return returnRecord) {
        List<Return_item> returnItems = returnItemRepository.findByReturnRecord(returnRecord);
        List<ReturnItemRes> returnItemResponses = new ArrayList<>();

        for (Return_item item : returnItems) {
            ReturnItemRes itemRes = new ReturnItemRes();
            itemRes.setId(item.getId());
            itemRes.setItemId(item.getItemId());
            itemRes.setQuantity(item.getQuantity());
            itemRes.setQuantityLitres(item.getQuantityLitres());
            itemRes.setQuantityMiliLitres(item.getQuantityMiliLitres());
            itemRes.setIsLoose(item.getIsLoose());
            returnItemResponses.add(itemRes);
        }

        ReturnRes response = new ReturnRes();
        response.setId(returnRecord.getId());
        response.setRONumber(returnRecord.getRONumber());
        response.setCreatedAt(returnRecord.getCreatedAt());
        response.setItems(returnItemResponses);
        return response;
    }

    private void restoreItemStock(Item item, Integer quantity, Double quantityLitres, Double quantityMillilitres) {
        BigDecimal packSizeInLiters = BigDecimal.valueOf(getPackSizeInLiters(item));
        BigDecimal stockInLiters = BigDecimal.valueOf(item.getStockInLiters());

        // Set default values for null quantities
        quantity = (quantity != null) ? quantity : 0;
        quantityLitres = (quantityLitres != null) ? quantityLitres : 0.0;
        quantityMillilitres = (quantityMillilitres != null) ? quantityMillilitres : 0.0;

        BigDecimal totalRestoreLiters = BigDecimal.ZERO;

        if (quantity > 0) {
            totalRestoreLiters = totalRestoreLiters.add(packSizeInLiters.multiply(BigDecimal.valueOf(quantity)));
        }

        // For loose quantities, use EITHER litres OR millilitres, not both
        if (quantityLitres > 0) {
            totalRestoreLiters = totalRestoreLiters.add(BigDecimal.valueOf(quantityLitres));
        } else if (quantityMillilitres > 0) {
            totalRestoreLiters = totalRestoreLiters.add(BigDecimal.valueOf(quantityMillilitres)
                    .divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP));
        }

        BigDecimal newStockInLiters = stockInLiters.add(totalRestoreLiters);

        item.setStockInLiters(newStockInLiters.setScale(6, RoundingMode.HALF_UP).doubleValue());
        item.setStockInMillilitres(newStockInLiters.multiply(BigDecimal.valueOf(1000))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue());

        int newPackQuantity = newStockInLiters.divide(packSizeInLiters, 0, RoundingMode.DOWN).intValue();
        item.setAvailableStock(newPackQuantity);
    }

    @Override
    public String getLastReturnNumber() {
        return returnRepository.findTopByOrderByIdDesc()
                .map(Return::getRONumber)
                .orElse("RO-0");
    }
}
