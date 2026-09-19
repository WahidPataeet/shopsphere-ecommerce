package com.shopsphere.product.service;

import com.shopsphere.product.dto.request.CreateProductRequest;
import com.shopsphere.product.dto.request.UpdateProductRequest;
import com.shopsphere.product.dto.response.PageResponse;
import com.shopsphere.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse getProductById(Long id);

    PageResponse<ProductResponse> getAllProducts(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String name,
            String status,
            Long categoryId,
            Long brandId
    );

    ProductResponse updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);
}
