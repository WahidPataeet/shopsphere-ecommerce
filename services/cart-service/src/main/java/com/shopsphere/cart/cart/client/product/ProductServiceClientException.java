package com.shopsphere.cart.cart.client.product;

public class ProductServiceClientException
        extends RuntimeException{

    ProductServiceClientException(String message) {
        super(message);
    }

    ProductServiceClientException(
           String message,
           Throwable cause) {

        super(message,cause);
    }
}
