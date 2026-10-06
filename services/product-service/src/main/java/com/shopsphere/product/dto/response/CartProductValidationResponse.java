package com.shopsphere.product.dto.response;

public record CartProductValidationResponse (

        Long productId,

        Long variantId,

        boolean productActive,

        boolean variantActive,

        boolean variantBelongsToProduct
){
}
