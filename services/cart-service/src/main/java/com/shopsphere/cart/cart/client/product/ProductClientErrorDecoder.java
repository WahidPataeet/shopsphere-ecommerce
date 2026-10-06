package com.shopsphere.cart.cart.client.product;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class ProductClientErrorDecoder
        implements ErrorDecoder {

    @Override
    public Exception decode(
            String methodKey,
            Response response) {

        if (response.status() == 404) {

            return new ProductServiceClientException(
                    "Product or variant not found"
            );
        }

        if (response.status() >= 500) {

            return new ProductServiceClientException(
                    "Product Service is currently unavailable"
            );
        }

        return new ProductServiceClientException(
                "Product Service request failed with status: "
                        + response.status()
        );
    }
}
