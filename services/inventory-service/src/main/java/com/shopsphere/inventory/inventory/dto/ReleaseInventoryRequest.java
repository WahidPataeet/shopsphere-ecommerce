package com.shopsphere.inventory.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReleaseInventoryRequest(

        @NotNull
        @Min(1)
        Integer quantity
) {
}