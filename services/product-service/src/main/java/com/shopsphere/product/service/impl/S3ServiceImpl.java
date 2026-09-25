package com.shopsphere.product.service.impl;

import com.shopsphere.product.exception.S3OperationException;
import com.shopsphere.product.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public String uploadFile(
            MultipartFile file,
            String objectKey) {

        try {

            PutObjectRequest request =
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );

            return objectKey;

        } catch (IOException |
                 S3Exception |
                 SdkClientException exception) {

            throw new S3OperationException(
                    "Failed to upload file to S3",
                    exception
            );
        }
    }

    @Override
    public void deleteFile(String objectKey) {

        try {

            DeleteObjectRequest request =
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build();

            s3Client.deleteObject(request);

        } catch (S3Exception |
                 SdkClientException exception) {

            throw new S3OperationException(
                    "Failed to delete file from S3",
                    exception
            );
        }
    }

    @Override
    public String generateDownloadUrl(String objectKey) {

        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .getObjectRequest(getObjectRequest)
                        .build();

        return s3Presigner
                .presignGetObject(presignRequest)
                .url()
                .toString();
    }
}
