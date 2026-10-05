package com.shopsphere.cart.cart.controller;

import com.shopsphere.cart.cart.dto.AddCartItemRequest;
import com.shopsphere.cart.cart.dto.CartResponse;
import com.shopsphere.cart.cart.dto.UpdateCartItemRequest;
import com.shopsphere.cart.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                cartService.getCart(userId)
        );
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            Authentication authentication,
            @Valid @RequestBody AddCartItemRequest request) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                cartService.addItem(
                        userId,
                        request
                )
        );
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(
            Authentication authentication,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        Long userId =
                (Long) authentication.getPrincipal();

        return ResponseEntity.ok(
                cartService.updateItem(
                        userId,
                        itemId,
                        request
                )
        );
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            Authentication authentication,
            @PathVariable Long itemId) {

        Long userId =
                (Long) authentication.getPrincipal();

        cartService.removeItem(userId, itemId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items")
    public ResponseEntity<Void> clearCart(
            Authentication authentication) {

        Long userId =
                (Long) authentication.getPrincipal();

        cartService.clearCart(userId);

        return ResponseEntity.noContent().build();
    }
}
