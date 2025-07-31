package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class GRNItemSaveRequest {
    private Long grnId; // ID of the GRN this item belongs to
    private Long itemId; // ID of the item being added to the GRN
    private String supplier_name; // Name of the supplier for the item
    private double quantity; // Quantity of the item in the GRN
    private double unitPrice; // Price per unit of the item
    private double totalPrice; // Total price for this item (quantity * unitPrice)
    private String createdAt; // Timestamp of creation, consider using LocalDateTime for better date handling
}
