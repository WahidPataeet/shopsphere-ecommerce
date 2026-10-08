package com.shopsphere.inventory.inventory.service;

import com.shopsphere.inventory.exception.InsufficientStockException;
import com.shopsphere.inventory.inventory.dto.ReserveInventoryRequest;
import com.shopsphere.inventory.inventory.entity.Inventory;
import com.shopsphere.inventory.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class InventoryConcurrencyTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    private static final Long VARIANT_ID = 999L;

    @BeforeEach
    void setUp() {

        inventoryRepository.findByVariantId(VARIANT_ID)
                .ifPresent(inventoryRepository::delete);

        inventoryRepository.flush();

        Inventory inventory = Inventory.builder()
                .variantId(VARIANT_ID)
                .totalStock(1)
                .reservedStock(0)
                .build();

        inventoryRepository.saveAndFlush(inventory);
    }

    @Test
    void shouldPreventOverselling() throws Exception {

        int numberOfRequests = 2;

        ExecutorService executorService =
                Executors.newFixedThreadPool(numberOfRequests);

        CountDownLatch startLatch =
                new CountDownLatch(1);

        List<Callable<String>> tasks = new ArrayList<>();

        for (int i = 0; i < numberOfRequests; i++) {

            tasks.add(() -> {

                startLatch.await();

                try {

                    inventoryService.reserveStock(
                            VARIANT_ID,
                            new ReserveInventoryRequest(1)
                    );

                    return "SUCCESS";

                } catch (InsufficientStockException exception) {

                    return "INSUFFICIENT_STOCK";
                }
            });
        }

        List<Future<String>> futures = new ArrayList<>();

        for (Callable<String> task : tasks) {
            futures.add(executorService.submit(task));
        }

        startLatch.countDown();

        List<String> results = new ArrayList<>();

        for (Future<String> future : futures) {
            results.add(future.get(10, TimeUnit.SECONDS));
        }

        executorService.shutdown();

        long successfulReservations = results.stream()
                .filter("SUCCESS"::equals)
                .count();

        long rejectedReservations = results.stream()
                .filter("INSUFFICIENT_STOCK"::equals)
                .count();

        assertEquals(
                1,
                successfulReservations,
                "Exactly one reservation should succeed"
        );

        assertEquals(
                1,
                rejectedReservations,
                "Exactly one reservation should be rejected"
        );

        Inventory finalInventory =
                inventoryRepository.findByVariantId(VARIANT_ID)
                        .orElseThrow();

        assertEquals(
                1,
                finalInventory.getReservedStock(),
                "Reserved stock must never exceed available stock"
        );

        assertEquals(
                0,
                finalInventory.getAvailableStock(),
                "Available stock should be zero after successful reservation"
        );
    }
}
