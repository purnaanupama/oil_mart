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
    public SalesOrderResponse saveSalesOrder(SalesOrderSaveRequest request) {
        Sales_Order salesOrder = new Sales_Order();
        salesOrder.setSalesOrderNo(request.getSalesOrderNo());
        salesOrder.setSalesOrderType(request.getSalesOrderType());
        salesOrder.setTotalAmount(request.getTotalAmount());
        salesOrder.setCustomer(customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.getCustomerId())));
        salesOrder.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now().toString());
        salesOrder.setNote(request.getNote());

        salesOrder = salesOrderRepository.save(salesOrder);

        if (request.getItems() != null) {
            Sales_Order finalSalesOrder = salesOrder;
            List<Sales_Order_Item> orderItems = request.getItems().stream().map(itemReq -> {
                Sales_Order_Item item = new Sales_Order_Item();
                item.setSalesOrder(finalSalesOrder);

                Item foundItem = itemRepository.findById(itemReq.getItemId());
                if (foundItem == null) {
                    throw new RuntimeException("Item not found with ID: " + itemReq.getItemId());
                }

                double newStock = foundItem.getAvailableStock() - itemReq.getQuantity();
                if (newStock < 0) {
                    throw new RuntimeException("Insufficient stock for item ID: " + itemReq.getItemId());
                }
                foundItem.setAvailableStock(newStock);
                itemRepository.save(foundItem);

                item.setItem(foundItem);
                item.setQuantity(itemReq.getQuantity());
                item.setSoItemUnitPrice(itemReq.getSoItemUnitPrice());
                item.setSoItemTotalAmount(itemReq.getSoItemTotalAmount());
                item.setCreatedAt(itemReq.getCreatedAt() != null ? itemReq.getCreatedAt() : LocalDateTime.now().toString());
                return item;
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
    public void deleteSalesOrderById(Long salesOrderId) {
        Sales_Order salesOrder = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new RuntimeException("Sales order not found with ID: " + salesOrderId));

        List<Sales_Order_Item> orderItems = salesOrderItemRepository.findAllBySalesOrder(salesOrder);
        for (Sales_Order_Item orderItem : orderItems) {
            Item item = orderItem.getItem();
            item.setAvailableStock(item.getAvailableStock() + orderItem.getQuantity());
            itemRepository.save(item);
        }
        salesOrderItemRepository.deleteAll(orderItems);
        salesOrderRepository.delete(salesOrder);
    }

    @Override
    public SalesOrderResponse updateSalesOrder(SalesOrderUpdateRequest request) {
        Sales_Order salesOrder = salesOrderRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Sales order not found with ID: " + request.getId()));
        salesOrder.setSalesOrderNo(request.getSalesOrderNo());
        salesOrder.setSalesOrderType(request.getSalesOrderType());
        salesOrder.setTotalAmount(request.getTotalAmount());
        salesOrder.setCustomer(customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + request.getCustomerId())));
        salesOrder.setCreatedAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now().toString());
        salesOrder.setNote(request.getNote());

        salesOrder = salesOrderRepository.save(salesOrder);

        if (request.getItems() != null) {
            List<Sales_Order_Item> orderItems = request.getItems().stream().map(itemReq -> {
                Sales_Order_Item item = salesOrderItemRepository.findById(itemReq.getItemId())
                        .orElseThrow(() -> new RuntimeException("Sales order item not found with ID: " + itemReq.getItemId()));

                Item foundItem = itemRepository.findById(itemReq.getItemId());
                if (foundItem == null) {
                    throw new RuntimeException("Item not found with ID: " + itemReq.getItemId());
                }

                double newStock = foundItem.getAvailableStock() - itemReq.getQuantity();
                if (newStock < 0) {
                    throw new RuntimeException("Insufficient stock for item ID: " + itemReq.getItemId());
                }
                foundItem.setAvailableStock(newStock);
                itemRepository.save(foundItem);

                item.setItem(foundItem);
                item.setQuantity(itemReq.getQuantity());
                item.setSoItemUnitPrice(itemReq.getSoItemUnitPrice());
                item.setSoItemTotalAmount(itemReq.getSoItemTotalAmount());
                item.setCreatedAt(itemReq.getCreatedAt() != null ? itemReq.getCreatedAt() : LocalDateTime.now().toString());
                return item;
            }).toList();

            salesOrderItemRepository.saveAll(orderItems);
        }

        return buildSalesOrderResponse(salesOrder);
    }

    @Override
    public String getLastSalesOrderNumber() {
        Sales_Order lastOrder = salesOrderRepository.findTopByOrderByIdDesc();
        return lastOrder != null ? lastOrder.getSalesOrderNo() : "SAL-2025-001";
    }

    //Only Update Sales Order Item
    @Override
    public SalesOrderResponse updateSalesOrderItem(SalesOrderItemUpdateRequest request) {
        Sales_Order salesOrder = salesOrderRepository.findById(request.getSalesOrderId())
                .orElseThrow(() -> new RuntimeException("Sales order not found with ID: " + request.getSalesOrderId()));

        Sales_Order_Item orderItem = salesOrderItemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Sales order item not found with ID: " + request.getItemId()));

        if (!orderItem.getSalesOrder().getId().equals(salesOrder.getId())) {
            throw new RuntimeException("Sales order item does not belong to the specified sales order.");
        }

        Item item = orderItem.getItem();
        double previousQuantity = orderItem.getQuantity();
        double newStock = item.getAvailableStock() + previousQuantity - request.getQuantity();

        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock for item ID: " + item.getId());
        }

        item.setAvailableStock(newStock);
        itemRepository.save(item);

        orderItem.setQuantity(request.getQuantity());
        orderItem.setSoItemUnitPrice(request.getSoItemUnitPrice());
        orderItem.setSoItemTotalAmount(request.getSoItemTotalAmount());
        orderItem.setCreatedAt(request.getUpdatedAt() != null ? request.getUpdatedAt() : LocalDateTime.now().toString());

        salesOrderItemRepository.save(orderItem);

        return buildSalesOrderResponse(salesOrder);
    }

    //Delete Sales Order Item
    @Override
    public void deleteSalesOrderItemById(Long salesOrderId, Long itemId) {
        Sales_Order salesOrder = salesOrderRepository.findById(salesOrderId)
                .orElseThrow(() -> new RuntimeException("Sales order not found with ID: " + salesOrderId));

        Sales_Order_Item orderItem = salesOrderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Sales order item not found with ID: " + itemId));

        if (!orderItem.getSalesOrder().getId().equals(salesOrder.getId())) {
            throw new RuntimeException("Sales order item does not belong to the specified sales order.");
        }

        Item item = orderItem.getItem();
        item.setAvailableStock(item.getAvailableStock() + orderItem.getQuantity());
        itemRepository.save(item);

        salesOrderItemRepository.delete(orderItem);
    }

    // Build DTO from entity
    private SalesOrderResponse buildSalesOrderResponse(Sales_Order salesOrder) {
        SalesOrderResponse response = new SalesOrderResponse();
        response.setId(salesOrder.getId());
        response.setSalesOrderNo(salesOrder.getSalesOrderNo());
        response.setSalesOrderType(salesOrder.getSalesOrderType());
        response.setTotalAmount(salesOrder.getTotalAmount());
        response.setCustomerId(salesOrder.getCustomer().getId());
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
        response.setSoItemUnitPrice(item.getSoItemUnitPrice());
        response.setSoItemTotalAmount(item.getSoItemTotalAmount());
        response.setCreatedAt(item.getCreatedAt());
        return response;
    }
}
