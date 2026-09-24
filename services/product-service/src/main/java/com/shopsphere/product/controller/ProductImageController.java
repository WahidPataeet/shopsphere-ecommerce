package com.shopsphere.product.controller;

import com.shopsphere.product.dto.response.ProductImageResponse;
import com.shopsphere.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductImageResponse> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) {

        ProductImageResponse response =
                productImageService.uploadImage(
                        productId,
                        file
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductImageResponse>> getImages(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                productImageService.getProductImages(productId)
        );
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        productImageService.deleteImage(
                productId,
                imageId
        );

        return ResponseEntity.noContent().build();
    }
}
