package com.shopsphere.inventory.inventory.service.impl;

import com.shopsphere.inventory.exception.InsufficientStockException;
import com.shopsphere.inventory.exception.ResourceNotFoundException;
import com.shopsphere.inventory.inventory.dto.*;
import com.shopsphere.inventory.inventory.entity.Inventory;
import com.shopsphere.inventory.inventory.repository.InventoryRepository;
import com.shopsphere.inventory.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public InventoryResponse createInventory(CreateInventoryRequest request) {

        if (inventoryRepository.existsByVariantId(request.variantId())) {
            throw new IllegalArgumentException(
                    "Inventory already exists for variant: " + request.variantId()
            );
        }

        Inventory inventory = Inventory.builder()
                .variantId(request.variantId())
                .totalStock(request.totalStock())
                .reservedStock(0)
                .build();

        Inventory savedInventory = inventoryRepository.save(inventory);

        return toResponse(savedInventory);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long variantId) {

        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for variant: " + variantId
                        )
                );

        return toResponse(inventory);
    }

    @Override
    public InventoryResponse updateInventory(
            Long variantId,
            UpdateInventoryRequest request
    ) {

        Inventory inventory = inventoryRepository.findByVariantIdForUpdate(variantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for variant: " + variantId
                        )
                );

        if (request.totalStock() < inventory.getReservedStock()) {
            throw new IllegalArgumentException(
                    "Total stock cannot be less than reserved stock"
            );
        }

        inventory.setTotalStock(request.totalStock());

        return toResponse(inventory);
    }

    @Override
    public InventoryResponse reserveStock(
            Long variantId,
            ReserveInventoryRequest request
    ) {

        log.info(
                "Attempting stock reservation. variantId={}, quantity={}",
                variantId,
                request.quantity()
        );

        Inventory inventory =
                inventoryRepository.findByVariantIdForUpdate(variantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Inventory not found for variant: "
                                                + variantId
                                )
                        );

        int availableStock = inventory.getAvailableStock();

        if (availableStock < request.quantity()) {

            log.warn(
                    "Insufficient stock. variantId={}, requested={}, available={}",
                    variantId,
                    request.quantity(),
                    availableStock
            );

            throw new InsufficientStockException(
                    "Insufficient stock for variant: " + variantId
            );
        }

        inventory.setReservedStock(
                inventory.getReservedStock() + request.quantity()
        );

        log.info(
                "Stock reserved successfully. variantId={}, quantity={}, reservedStock={}, availableStock={}",
                variantId,
                request.quantity(),
                inventory.getReservedStock(),
                inventory.getAvailableStock()
        );

        return toResponse(inventory);
    }

    @Override
    public InventoryResponse releaseStock(
            Long variantId,
            ReleaseInventoryRequest request
    ) {

        Inventory inventory = inventoryRepository.findByVariantIdForUpdate(variantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for variant: " + variantId
                        )
                );

        if (request.quantity() > inventory.getReservedStock()) {
            throw new IllegalArgumentException(
                    "Cannot release more stock than reserved stock"
            );
        }

        inventory.setReservedStock(
                inventory.getReservedStock() - request.quantity()
        );

        return toResponse(inventory);
    }

    private InventoryResponse toResponse(Inventory inventory) {

        return new InventoryResponse(
                inventory.getId(),
                inventory.getVariantId(),
                inventory.getTotalStock(),
                inventory.getReservedStock(),
                inventory.getAvailableStock()
        );
    }
}
