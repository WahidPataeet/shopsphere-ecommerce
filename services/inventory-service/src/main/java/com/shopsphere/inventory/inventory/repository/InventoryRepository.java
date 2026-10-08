package com.shopsphere.inventory.inventory.repository;

import com.shopsphere.inventory.inventory.entity.Inventory;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository
        extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByVariantId(Long variantId);

    boolean existsByVariantId(Long variantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.variantId = :variantId
            """)
    Optional<Inventory> findByVariantIdForUpdate(
            @Param("variantId") Long variantId
    );
}