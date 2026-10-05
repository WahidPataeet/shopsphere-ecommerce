package com.shopsphere.cart.cart.repository;

import com.shopsphere.cart.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductIdAndVariantId(
            Long cartId,
            Long productId,
            Long variantId
    );
}
