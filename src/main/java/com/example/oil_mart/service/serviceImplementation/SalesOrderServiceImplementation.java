package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.SalesOrderItemUpdateRequest;
import com.example.oil_mart.dto.request.SalesOrderSaveRequest;
import com.example.oil_mart.dto.request.SalesOrderUpdateRequest;
import com.example.oil_mart.dto.response.SalesOrderItemResponse;
import com.example.oil_mart.dto.response.SalesOrderResponse;
import com.example.oil_mart.model.Item;
import com.example.oil_mart.model.Sales_Order;
import com.example.oil_mart.model.Sales_Order_Item;
import com.example.oil_mart.repository.CustomerRepository;
import com.example.oil_mart.repository.ItemRepository;
import com.example.oil_mart.repository.SalesOrderItemRepository;
import com.example.oil_mart.repository.SalesOrderRepository;
import com.example.oil_mart.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalesOrderServiceImplementation implements SalesOrderService {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private SalesOrderItemRepository salesOrderItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    @Transactional
    public SalesOrderResponse saveSalesOrder(SalesOrderSaveRequest request) {
        // Validate customer ID based on sales order type
        if ("CREDIT".equalsIgnoreCase(request.getSalesOrderType())) {
            if (request.getCustomerId() == null) {
                throw new RuntimeException("Customer ID is required for CREDIT sales");
            }
        }

        // 1️⃣ Create Sales Order Header
        Sales_Order salesOrder = new Sales_Order();
        salesOrder.setSalesOrderNo(request.getSalesOrderNo());
        salesOrder.setSalesOrderType(request.getSalesOrderType());
        salesOrder.setTotalAmount(request.getTotalAmount());

        // Set customer only for CREDIT sales
        if ("CREDIT".equalsIgnoreCase(request.getSalesOrderType()) && request.getCustomerId() != null) {
            salesOrder.setCustomer(customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.getCustomerId())));
        } else {
            salesOrder.setCustomer(null); // No customer for CASH sales
        }

        salesOrder.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now().toString());
        salesOrder.setNote(request.getNote());
        salesOrder = salesOrderRepository.save(salesOrder);

        // 2️⃣ Process Sales Order Items
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            Sales_Order finalSalesOrder = salesOrder;

            List<Sales_Order_Item> orderItems = request.getItems().stream().map(itemReq -> {
                Sales_Order_Item orderItem = new Sales_Order_Item();
                orderItem.setSalesOrder(finalSalesOrder);

                // Validate item ID
                if (itemReq.getItemId() == null) {
                    throw new RuntimeException("Item ID cannot be null");
                }

                // Fetch the item
                Item foundItem = itemRepository.findById(itemReq.getItemId())
                        .orElseThrow(() -> new RuntimeException("Item not found with ID: " + itemReq.getItemId()));

                // Deduct stock using liquid-based calculation
                deductItemStock(foundItem, itemReq.getQuantity(), itemReq.getQuantityLiters(), itemReq.getQuantityMilliliters());

                // Save updated stock
                itemRepository.save(foundItem);

                // --- Save sales order item ---
                orderItem.setItem(foundItem);
                orderItem.setQuantity(itemReq.getQuantity() != null ? itemReq.getQuantity() : 0);
                orderItem.setQuantityLiters(itemReq.getQuantityLiters());

                // Convert Integer to Integer for quantityMilliliters
                if (itemReq.getQuantityMilliliters() != null) {
                    orderItem.setQuantityMilliliters(itemReq.getQuantityMilliliters());
                } else {
                    orderItem.setQuantityMilliliters(null);
                }

                orderItem.setIsLoose(itemReq.getIsLoose() != null ? itemReq.getIsLoose() : false);
                orderItem.setSoItemUnitPrice(itemReq.getSoItemUnitPrice());
                orderItem.setSoItemTotalAmount(itemReq.getSoItemTotalAmount());
                orderItem.setCreatedAt(itemReq.getCreatedAt() != null ? itemReq.getCreatedAt() : LocalDateTime.now().toString());

                return orderItem;
            }).toList();

            salesOrderItemRepository.saveAll(orderItems);
        }

        return buildSalesOrderResponse(salesOrder);
    }

    @Override
    public List<SalesOrderResponse> getSalesOrders() {
        return salesOrderRepository.findAll()
                .stream()
                .map(this::buildSalesOrderResponse)
                .toList();
    }

    @Override
    public SalesOrderResponse getSalesOrderById(Long id) {
        Sales_Order salesOrder = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found with ID: " + id));
        return buildSalesOrderResponse(salesOrder);
    }

    @Override
    @Transactional
    public void deleteSalesOrderById(Long salesOrderId) {
        Sales_Order salesOrder = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new RuntimeException("Sales order not found with ID: " + salesOrderId));

        // Restore stock for all items in the order
        List<Sales_Order_Item> orderItems = salesOrderItemRepository.findAllBySalesOrder(salesOrder);
        for (Sales_Order_Item orderItem : orderItems) {
            Item item = orderItem.getItem();

            // Restore stock using the same liquid-based logic
            restoreItemStock(item, orderItem.getQuantity(), orderItem.getQuantityLiters(),
                    orderItem.getQuantityMilliliters());

            itemRepository.save(item);
        }

        // Delete order items first, then the order
        salesOrderItemRepository.deleteAll(orderItems);
        salesOrderRepository.delete(salesOrder);
    }

    @Override
    public String getLastOrderNumber() {
        Sales_Order lastOrder = salesOrderRepository.findTopByOrderByIdDesc();
        return lastOrder != null ? lastOrder.getSalesOrderNo() : "SAL-2025-001";
    }


    @Override
    @Transactional
    public void deleteSalesOrderItemById(Long salesOrderId, Long itemId) {
        Sales_Order salesOrder = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new RuntimeException("Sales order not found with ID: " + salesOrderId));

        Sales_Order_Item orderItem = salesOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Sales order item not found with ID: " + itemId));

        if (!orderItem.getSalesOrder().getId().equals(salesOrder.getId())) {
            throw new RuntimeException("Sales order item does not belong to the specified sales order.");
        }

        Item item = orderItem.getItem();

        // Restore stock
        restoreItemStock(item, orderItem.getQuantity(), orderItem.getQuantityLiters(),
                orderItem.getQuantityMilliliters());
        itemRepository.save(item);

        salesOrderItemRepository.delete(orderItem);
    }

    private void deductItemStock(Item item, Integer quantity, Double quantityLiters, Integer quantityMilliliters) {
        BigDecimal packSizeInLiters = BigDecimal.valueOf(getPackSizeInLiters(item));
        BigDecimal stockInLiters = BigDecimal.valueOf(item.getStockInLiters());

        // Calculate total liters to deduct
        BigDecimal totalDeductLiters = BigDecimal.ZERO;
        if (quantity != null && quantity > 0) {
            totalDeductLiters = totalDeductLiters.add(packSizeInLiters.multiply(BigDecimal.valueOf(quantity)));
        }
        if (quantityLiters != null) {
            totalDeductLiters = totalDeductLiters.add(BigDecimal.valueOf(quantityLiters));
        }
        if (quantityMilliliters != null) {
            totalDeductLiters = totalDeductLiters.add(BigDecimal.valueOf(quantityMilliliters)
                    .divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP));
        }

        // Deduct stock
        BigDecimal newStockInLiters = stockInLiters.subtract(totalDeductLiters);
        if (newStockInLiters.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient stock for item ID: " + item.getId());
        }

        // Update item stock
        item.setStockInLiters(newStockInLiters.setScale(6, RoundingMode.HALF_UP).doubleValue());
        item.setStockInMillilitres(newStockInLiters.multiply(BigDecimal.valueOf(1000))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue());

        // Update available packs
        int newPackQuantity = newStockInLiters.divide(packSizeInLiters, 0, RoundingMode.DOWN).intValue();
        item.setAvailableStock(newPackQuantity);
    }

    // Helper method to deduct stock using liquid-based calculations
//    private void deductItemStock(Item item, Integer quantity, Double quantityLiters, Integer quantityMilliliters) {
//        // Parse pack size to liters
//        double packSizeInLiters = getPackSizeInLiters(item);
//
//        // Calculate total liters to deduct
//        double totalDeductLiters = 0.0;
//        if (quantity != null && quantity > 0) {
//            totalDeductLiters += quantity * packSizeInLiters;
//        }
//        if (quantityLiters != null) {
//            totalDeductLiters += quantityLiters;
//        }
//        if (quantityMilliliters != null) {
//            totalDeductLiters += quantityMilliliters / 1000.0;
//        }
//
//        // Check if we have enough liquid stock
//        double newStockInLiters = item.getStockInLiters() - totalDeductLiters;
//        if (newStockInLiters < 0) {
//            throw new RuntimeException("Insufficient stock (liters) for item ID: " + item.getId() +
//                    ". Required: " + totalDeductLiters + "L, Available: " + item.getStockInLiters() + "L");
//        }
//
//        // Update liquid stock first
//        item.setStockInLiters(newStockInLiters);
//        item.setStockInMillilitres(newStockInLiters * 1000);
//
//        // Calculate new pack quantity based on remaining liquid stock
//        // Full packs = floor(remaining liquid / pack size)
//        int newPackQuantity = (int) Math.floor(newStockInLiters / packSizeInLiters);
//
//        // Validate we have enough packs for full pack sales
//        if (quantity != null && quantity > 0 && item.getAvailableStock() < quantity) {
//            throw new RuntimeException("Insufficient stock (packs) for item ID: " + item.getId() +
//                    ". Required: " + quantity + ", Available: " + item.getAvailableStock());
//        }
//
//        // Update pack quantity
//        item.setAvailableStock(newPackQuantity);
//    }

    private void restoreItemStock(Item item, Integer quantity, Double quantityLiters, Integer quantityMilliliters) {
        BigDecimal packSizeInLiters = BigDecimal.valueOf(getPackSizeInLiters(item));
        BigDecimal stockInLiters = BigDecimal.valueOf(item.getStockInLiters());

        // Calculate total liters to restore
        BigDecimal totalRestoreLiters = BigDecimal.ZERO;
        if (quantity != null && quantity > 0) {
            totalRestoreLiters = totalRestoreLiters.add(packSizeInLiters.multiply(BigDecimal.valueOf(quantity)));
        }
        if (quantityLiters != null) {
            totalRestoreLiters = totalRestoreLiters.add(BigDecimal.valueOf(quantityLiters));
        }
        if (quantityMilliliters != null) {
            totalRestoreLiters = totalRestoreLiters.add(BigDecimal.valueOf(quantityMilliliters)
                    .divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP));
        }

        // Restore stock
        BigDecimal newStockInLiters = stockInLiters.add(totalRestoreLiters);

        // Update item stock with precise scale
        item.setStockInLiters(newStockInLiters.setScale(6, RoundingMode.HALF_UP).doubleValue());
        item.setStockInMillilitres(newStockInLiters.multiply(BigDecimal.valueOf(1000))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue());

        // Update available packs
        int newPackQuantity = newStockInLiters.divide(packSizeInLiters, 0, RoundingMode.DOWN).intValue();
        item.setAvailableStock(newPackQuantity);
    }

    // Helper method to restore stock using liquid-based calculations
//    private void restoreItemStock(Item item, Integer quantity, Double quantityLiters, Integer quantityMilliliters) {
//        // Parse pack size to liters
//        double packSizeInLiters = getPackSizeInLiters(item);
//
//        // Calculate total liters to restore
//        double totalRestoreLiters = 0.0;
//        if (quantity != null && quantity > 0) {
//            totalRestoreLiters += quantity * packSizeInLiters;
//        }
//        if (quantityLiters != null) {
//            totalRestoreLiters += quantityLiters;
//        }
//        if (quantityMilliliters != null) {
//            totalRestoreLiters += quantityMilliliters / 1000.0;
//        }
//
//        // Update liquid stock first
//        double newStockInLiters = item.getStockInLiters() + totalRestoreLiters;
//        item.setStockInLiters(newStockInLiters);
//        item.setStockInMillilitres(newStockInLiters * 1000);
//
//        // Calculate new pack quantity based on total liquid stock
//        // Full packs = floor(total liquid / pack size)
//        int newPackQuantity = (int) Math.floor(newStockInLiters / packSizeInLiters);
//        item.setAvailableStock(newPackQuantity);
//    }

    // Helper method to get pack size in liters
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

    // Build DTO from entity
    private SalesOrderResponse buildSalesOrderResponse(Sales_Order salesOrder) {
        SalesOrderResponse response = new SalesOrderResponse();
        response.setId(salesOrder.getId());
        response.setSalesOrderNo(salesOrder.getSalesOrderNo());
        response.setSalesOrderType(salesOrder.getSalesOrderType());
        response.setTotalAmount(salesOrder.getTotalAmount());
        response.setCustomerId(salesOrder.getCustomer() != null ? salesOrder.getCustomer().getId() : null);
        response.setCreatedAt(salesOrder.getCreatedAt());
        response.setNote(salesOrder.getNote());

        List<SalesOrderItemResponse> itemResponses = salesOrderItemRepository.findAllBySalesOrder(salesOrder)
                .stream()
                .map(this::entityToResponse)
                .toList();
        response.setItems(itemResponses);
        return response;
    }

    // Convert item entity to DTO
    private SalesOrderItemResponse entityToResponse(Sales_Order_Item item) {
        SalesOrderItemResponse response = new SalesOrderItemResponse();
        response.setId(item.getId());
        response.setItemId(Long.valueOf(item.getItem() != null ? item.getItem().getId() : null));
        response.setQuantity(item.getQuantity());
        response.setQuantityLiters(item.getQuantityLiters());
        response.setQuantityMilliliters(item.getQuantityMilliliters());
        response.setSoItemUnitPrice(item.getSoItemUnitPrice());
        response.setSoItemTotalAmount(item.getSoItemTotalAmount());
        response.setCreatedAt(item.getCreatedAt());
        response.setIsLoose(item.getIsLoose());
        return response;
    }
}