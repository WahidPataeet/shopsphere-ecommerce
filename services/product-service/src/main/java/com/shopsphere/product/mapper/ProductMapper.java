package com.shopsphere.product.mapper;

import com.shopsphere.product.dto.response.ProductResponse;
import com.shopsphere.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());

        response.setCategoryId(product.getCategory().getId());
        response.setCategoryName(product.getCategory().getName());

        response.setBrandId(product.getBrand().getId());
        response.setBrandName(product.getBrand().getName());

        response.setStatus(product.getStatus());

        return response;
    }
}
