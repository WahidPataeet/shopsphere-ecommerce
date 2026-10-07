package com.shopsphere.inventory.inventory.dto;

public record InventoryResponse(

        Long id,

        Long variantId,

        Integer totalStock,

        Integer reservedStock,

        Integer availableStock
) {
}