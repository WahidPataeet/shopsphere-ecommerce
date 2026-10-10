package com.shopsphere.order.order.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateOrderItemRequest(

        @NotNull @Positive
        Long productId,

        @NotNull @Positive
        Long variantId,

        @NotBlank @Size(max = 200)
        String productName,

        @NotBlank @Size(max = 100)
        String sku,

        @NotNull @DecimalMin("0.01")
        BigDecimal unitPrice,

        @NotNull @Min(1) @Max(100)
        Integer quantity
) {}