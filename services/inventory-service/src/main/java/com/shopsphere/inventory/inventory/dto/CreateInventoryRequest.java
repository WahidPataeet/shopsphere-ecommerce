package com.shopsphere.inventory.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateInventoryRequest(

        @NotNull
        Long variantId,

        @NotNull
        @Min(0)
        Integer totalStock
) {
}