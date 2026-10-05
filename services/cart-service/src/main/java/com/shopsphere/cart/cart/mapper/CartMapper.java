package com.shopsphere.cart.cart.mapper;

import com.shopsphere.cart.cart.dto.CartItemResponse;
import com.shopsphere.cart.cart.dto.CartResponse;
import com.shopsphere.cart.cart.entity.Cart;
import com.shopsphere.cart.cart.entity.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {

        List<CartItemResponse> items = cart.getItems()
                .stream()
                .map(this::toItemResponse)
                .toList();

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::quantity)
                .sum();

        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                cart.getStatus().name(),
                items,
                totalItems
        );
    }

    private CartItemResponse toItemResponse(CartItem item) {

        return new CartItemResponse(
                item.getId(),
                item.getProductId(),
                item.getVariantId(),
                item.getQuantity()
        );
    }
}
