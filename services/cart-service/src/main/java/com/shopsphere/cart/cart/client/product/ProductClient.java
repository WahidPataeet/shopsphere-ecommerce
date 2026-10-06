package com.shopsphere.cart.cart.client.product;

import com.shopsphere.cart.cart.client.product.dto.CartProductValidationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        url = "${services.product-service.url}",
        configuration = ProductClientConfiguration.class
)
public interface ProductClient {

    @GetMapping(
            "/api/v1/products/{productId}/variants/{variantId}/cart-validation"
    )
    CartProductValidationResponse validateProductForCart(
            @PathVariable Long productId,
            @PathVariable Long variantId
    );
}
