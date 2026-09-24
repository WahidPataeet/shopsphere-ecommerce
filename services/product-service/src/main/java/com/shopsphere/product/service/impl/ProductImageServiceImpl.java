package com.shopsphere.product.service.impl;

import com.shopsphere.product.dto.response.ProductImageResponse;
import com.shopsphere.product.entity.Product;
import com.shopsphere.product.entity.ProductImage;
import com.shopsphere.product.exception.ResourceNotFoundException;
import com.shopsphere.product.repository.ProductImageRepository;
import com.shopsphere.product.repository.ProductRepository;
import com.shopsphere.product.service.ProductImageService;
import com.shopsphere.product.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final List<String> ALLOWED_CONTENT_TYPES =
            List.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            );

    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    private final S3Service s3Service;

    @Override
    public ProductImageResponse uploadImage(
            Long productId,
            MultipartFile file) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        validateFile(file);

        String extension = getExtension(
                file.getOriginalFilename()
        );

        String objectKey =
                "products/"
                        + productId
                        + "/"
                        + UUID.randomUUID()
                        + extension;

        s3Service.uploadFile(file, objectKey);

        ProductImage productImage = ProductImage.builder()
                .product(product)
                .s3Key(objectKey)
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .displayOrder(0)
                .status("ACTIVE")
                .build();

        ProductImage savedImage =
                productImageRepository.save(productImage);

        return mapToResponse(savedImage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponse> getProductImages(
            Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                    "Product not found with id: " + productId
            );
        }

        return productImageRepository
                .findByProductIdOrderByDisplayOrderAsc(productId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteImage(
            Long productId,
            Long imageId) {

        ProductImage image =
                productImageRepository.findById(imageId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Image not found with id: "
                                                + imageId
                                )
                        );

        if (!image.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException(
                    "Image does not belong to this product"
            );
        }

        s3Service.deleteFile(image.getS3Key());

        productImageRepository.delete(image);
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Image file is required"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Image size must not exceed 5MB"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(contentType)) {

            throw new IllegalArgumentException(
                    "Only JPEG, PNG and WebP images are allowed"
            );
        }
    }

    private String getExtension(String fileName) {

        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(
                fileName.lastIndexOf(".")
        );
    }

    private ProductImageResponse mapToResponse(
            ProductImage image) {

        return ProductImageResponse.builder()
                .id(image.getId())
                .productId(image.getProduct().getId())
                .originalFileName(image.getOriginalFileName())
                .contentType(image.getContentType())
                .fileSize(image.getFileSize())
                .displayOrder(image.getDisplayOrder())
                .imageUrl(
                        s3Service.generateDownloadUrl(
                                image.getS3Key()
                        )
                )
                .status(image.getStatus())
                .build();
    }
}
