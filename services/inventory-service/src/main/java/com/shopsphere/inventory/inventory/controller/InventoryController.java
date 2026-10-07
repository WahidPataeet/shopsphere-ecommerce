package com.shopsphere.inventory.inventory.controller;

import com.shopsphere.inventory.inventory.dto.*;
import com.shopsphere.inventory.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(
            @Valid @RequestBody CreateInventoryRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        inventoryService.createInventory(request)
                );
    }

    @GetMapping("/{variantId}")
    public ResponseEntity<InventoryResponse> getInventory(
            @PathVariable Long variantId) {

        return ResponseEntity.ok(
                inventoryService.getInventory(variantId)
        );
    }

    @PutMapping("/{variantId}")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Long variantId,
            @Valid @RequestBody UpdateInventoryRequest request) {

        return ResponseEntity.ok(
                inventoryService.updateInventory(
                        variantId,
                        request
                )
        );
    }

    @PostMapping("/{variantId}/reserve")
    public ResponseEntity<InventoryResponse> reserveStock(
            @PathVariable Long variantId,
            @Valid @RequestBody ReserveInventoryRequest request) {

        return ResponseEntity.ok(
                inventoryService.reserveStock(
                        variantId,
                        request
                )
        );
    }

    @PostMapping("/{variantId}/release")
    public ResponseEntity<InventoryResponse> releaseStock(
            @PathVariable Long variantId,
            @Valid @RequestBody ReleaseInventoryRequest request) {

        return ResponseEntity.ok(
                inventoryService.releaseStock(
                        variantId,
                        request
                )
        );
    }
}
