package com.shopsphere.inventory.inventory.repository;

import com.shopsphere.inventory.inventory.entity.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository
        extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByVariantId(Long variantId);

    boolean existsByVariantId(Long variantId);
}