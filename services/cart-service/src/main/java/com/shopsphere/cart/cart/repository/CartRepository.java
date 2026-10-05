package com.shopsphere.cart.cart.repository;

import com.shopsphere.cart.cart.entity.Cart;
import com.shopsphere.cart.cart.entity.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {

    Optional<Cart> findByUserIdAndStatus(
            Long userId,
            CartStatus status
    );
}
