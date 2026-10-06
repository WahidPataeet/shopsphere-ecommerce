package com.shopsphere.cart.cart.client.product.dto;

public record CartProductValidationResponse (

        Long productId,

        Long variantId,

        boolean productActive,

        boolean variantActive,

        boolean variantBelongsToProduct
) {
}
