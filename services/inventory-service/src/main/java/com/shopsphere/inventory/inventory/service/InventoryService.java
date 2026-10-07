package com.shopsphere.inventory.inventory.service;

import com.shopsphere.inventory.inventory.dto.*;

public interface InventoryService {

    InventoryResponse createInventory(
            CreateInventoryRequest request
    );

    InventoryResponse getInventory(
            Long variantId
    );

    InventoryResponse updateInventory(
            Long variantId,
            UpdateInventoryRequest request
    );

    InventoryResponse reserveStock(
            Long variantId,
            ReserveInventoryRequest request
    );

    InventoryResponse releaseStock(
            Long variantId,
            ReleaseInventoryRequest request
    );
}
