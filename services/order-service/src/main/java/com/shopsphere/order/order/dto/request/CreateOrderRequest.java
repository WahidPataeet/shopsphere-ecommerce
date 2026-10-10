package com.shopsphere.order.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(

        @NotNull
        @Positive
        Long userId,

        @NotBlank
        @Size(max = 150)
        String shippingRecipient,

        @NotBlank
        @Size(max = 30)
        String shippingPhone,

        @NotBlank
        @Size(max = 255)
        String shippingAddressLine1,

        @Size(max = 255)
        String shippingAddressLine2,

        @NotBlank
        @Size(max = 100)
        String shippingCity,

        @NotBlank
        @Size(max = 100)
        String shippingState,

        @NotBlank
        @Size(max = 20)
        String shippingPostalCode,

        @NotBlank
        @Size(max = 100)
        String shippingCountry,

        @NotEmpty
        List<@Valid CreateOrderItemRequest> items,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal shippingFee,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal discountAmount
) {}