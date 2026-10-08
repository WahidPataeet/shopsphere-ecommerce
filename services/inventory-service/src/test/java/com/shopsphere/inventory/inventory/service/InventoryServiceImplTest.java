package com.shopsphere.inventory.inventory.service;

import com.shopsphere.inventory.exception.InsufficientStockException;
import com.shopsphere.inventory.inventory.dto.ReserveInventoryRequest;
import com.shopsphere.inventory.inventory.entity.Inventory;
import com.shopsphere.inventory.inventory.repository.InventoryRepository;

import com.shopsphere.inventory.inventory.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @Test
    void shouldRejectReservationWhenStockIsInsufficient() {

        Inventory inventory = Inventory.builder()
                .id(1L)
                .variantId(100L)
                .totalStock(10)
                .reservedStock(8)
                .build();

        when(inventoryRepository.findByVariantIdForUpdate(100L))
                .thenReturn(Optional.of(inventory));

        ReserveInventoryRequest request =
                new ReserveInventoryRequest(5);

        assertThrows(
                InsufficientStockException.class,
                () -> inventoryService.reserveStock(100L, request)
        );

        verify(inventoryRepository)
                .findByVariantIdForUpdate(100L);
    }
}