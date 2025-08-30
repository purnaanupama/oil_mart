package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.SalesOrderItemUpdateRequest;
import com.example.oil_mart.dto.request.SalesOrderSaveRequest;
import com.example.oil_mart.dto.request.SalesOrderUpdateRequest;
import com.example.oil_mart.dto.response.SalesOrderItemResponse;
import com.example.oil_mart.dto.response.SalesOrderResponse;
import com.example.oil_mart.model.*;
import com.example.oil_mart.repository.*;
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

    @Autowired
    private SalesProfitRepository salesProfitRepository;

    @Autowired
    private GRNItemRepository grnItemRepository;

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
            salesOrder.setCustomer(null);
        }

        salesOrder.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now().toString());
        salesOrder.setNote(request.getNote());
        salesOrder = salesOrderRepository.save(salesOrder);

        // 2️⃣ Process Sales Order Items
        double totalProfit = 0.0;

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

            // 3️⃣ Calculate profit for each item
            for (int i = 0; i < orderItems.size(); i++) {
                Sales_Order_Item orderItem = orderItems.get(i);
                var itemRequest = request.getItems().get(i);

                double itemProfit = calculateItemProfit(orderItem, itemRequest);
                totalProfit += itemProfit;
            }
        }

        // 4️⃣ Save total profit
        saveSalesProfit(salesOrder.getSalesOrderNo(), salesOrder.getSalesOrderType(), totalProfit);


        return buildSalesOrderResponse(salesOrder);
    }

    private double calculateItemProfit(Sales_Order_Item orderItem, com.example.oil_mart.dto.request.SalesOrderItemSaveRequest itemRequest) {
        Item item = orderItem.getItem();

        // Get latest GRN price for this item
        GrnItem latestGrnItem = grnItemRepository
                .findTopByItemIdOrderByCreatedAtDesc(Long.valueOf(item.getId()))
                .orElseThrow(() -> new RuntimeException("No GRN record found for item: " + item.getItemCode()));

        double costPrice = latestGrnItem.getUnitPrice();
        double salePrice = orderItem.getSoItemTotalAmount();

        // Check if this is a loose sale or pack-based sale
        if (itemRequest.getIsLoose() != null && itemRequest.getIsLoose()) {
            // LOOSE SALE - calculate based on volume
            double packSizeInLiters = getPackSizeInLiters(item);
            double costPerMl = costPrice / (packSizeInLiters * 1000); // cost per ml

            // Calculate total ml sold
            double totalMlSold = 0;
            if (orderItem.getQuantity() != null && orderItem.getQuantity() > 0) {
                totalMlSold += orderItem.getQuantity() * packSizeInLiters * 1000;
            }
            if (orderItem.getQuantityLiters() != null) {
                totalMlSold += orderItem.getQuantityLiters() * 1000;
            }
            if (orderItem.getQuantityMilliliters() != null) {
                totalMlSold += orderItem.getQuantityMilliliters();
            }

            double totalCost = totalMlSold * costPerMl;
            return salePrice - totalCost;

        } else {
            // PACK-BASED SALE - direct unit calculation
            int quantitySold = orderItem.getQuantity() != null ? orderItem.getQuantity() : 0;
            double totalCost = quantitySold * costPrice;
            return salePrice - totalCost;
        }
    }

    /**
     * Save profit record to SalesProfit table
     */
    private void saveSalesProfit(String salesOrderNo, String salesOrderType, double totalProfit) {
        SalesProfit salesProfit = new SalesProfit();
        salesProfit.setSalesOrderNo(salesOrderNo);
        salesProfit.setSalesOrderType(salesOrderType);
        salesProfit.setTotalProfitAmount(totalProfit);
        salesProfit.setCreatedAt(LocalDateTime.now().toString());

        salesProfitRepository.save(salesProfit);
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