package com.shopsphere.cart.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(

        @NotNull
        Long productId,

        @NotNull
        Long variantId,

        @NotNull
        @Min(1)
        Integer quantity
) {
}
