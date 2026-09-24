package com.shopsphere.product.repository;

import com.shopsphere.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);
}

