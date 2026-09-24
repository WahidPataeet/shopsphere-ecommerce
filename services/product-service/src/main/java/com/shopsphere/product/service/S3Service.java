package com.shopsphere.product.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

    String uploadFile(MultipartFile file, String objectKey);

    void deleteFile(String objectKey);

    String generateDownloadUrl(String objectKey);
}
