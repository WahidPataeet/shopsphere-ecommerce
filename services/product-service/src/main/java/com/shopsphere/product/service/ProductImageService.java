package com.shopsphere.product.service;

import com.shopsphere.product.dto.response.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {

    ProductImageResponse uploadImage(Long productId, MultipartFile file);

    List<ProductImageResponse> getProductImages(Long productId);

    void deleteImage(Long productId, Long imageId);
}
