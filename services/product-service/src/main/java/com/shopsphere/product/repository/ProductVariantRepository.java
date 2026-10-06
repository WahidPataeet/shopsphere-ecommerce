package com.shopsphere.product.repository;

import com.shopsphere.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductVariantRepository
        extends JpaRepository<ProductVariant, Long> {

    Optional<ProductVariant> findByIdAndProductId(
            Long id,
            Long productId
    );
}
