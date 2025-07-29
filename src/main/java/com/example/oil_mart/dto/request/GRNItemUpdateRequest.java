package com.example.oil_mart.dto.request;

import lombok.Data;

@Data
public class GRNItemUpdateRequest {
    private Long id; // ID of the GRN item to update
    private Long grnId; // ID of the GRN this item belongs to
    private Long itemId; // ID of the item being updated in the GRN
    private String supplierName; // Name of the supplier for the item
    private double quantity; // Updated quantity of the item in the GRN
    private double unitPrice; // Updated price per unit of the item
    private double totalPrice; // Updated total price for this item (quantity * unitPrice)
}
