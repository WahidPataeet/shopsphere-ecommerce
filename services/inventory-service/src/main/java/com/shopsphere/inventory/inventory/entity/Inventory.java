package com.shopsphere.inventory.inventory.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_variant",
                        columnNames = "variant_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "variant_id",
            nullable = false
    )
    private Long variantId;

    @Column(
            name = "total_stock",
            nullable = false
    )
    private Integer totalStock;

    @Column(
            name = "reserved_stock",
            nullable = false
    )
    private Integer reservedStock;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (reservedStock == null) {
            reservedStock = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }

    @Transient
    public Integer getAvailableStock() {

        return totalStock - reservedStock;
    }
}