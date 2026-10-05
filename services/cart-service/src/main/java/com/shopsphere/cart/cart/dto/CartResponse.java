package com.shopsphere.cart.cart.dto;

import java.util.List;

public record CartResponse(

    Long id,

    Long userId,

    String status,

    List<CartItemResponse> items,

    Integer totalItems
){
}
