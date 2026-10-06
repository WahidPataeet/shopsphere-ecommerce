package com.shopsphere.cart.cart.client.product;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class ProductClientConfiguration {

    @Bean
    public ErrorDecoder productClientErrorDecoder() {

        return new ProductClientErrorDecoder();
    }

}
