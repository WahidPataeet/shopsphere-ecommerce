package com.shopsphere.product.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImageResponse {

    private Long id;

    private Long productId;

    private String originalFileName;

    private String contentType;

    private Long fileSize;

    private Integer displayOrder;

    private String imageUrl;

    private String status;
}
