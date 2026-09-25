package com.shopsphere.product.service;

import com.shopsphere.product.entity.Product;
import com.shopsphere.product.entity.ProductImage;
import com.shopsphere.product.exception.ResourceNotFoundException;
import com.shopsphere.product.repository.ProductImageRepository;
import com.shopsphere.product.repository.ProductRepository;
import com.shopsphere.product.service.impl.ProductImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductImageServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private S3Service s3Service;

    private ProductImageServiceImpl service;

    @BeforeEach
    void setUp() {

        service = new ProductImageServiceImpl(
                productRepository,
                productImageRepository,
                s3Service
        );
    }

    @Test
    void uploadImage_shouldUploadSuccessfully() {

        Product product = Product.builder()
                .id(1L)
                .build();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "iphone.jpg",
                        "image/jpeg",
                        "test image content".getBytes()
                );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productImageRepository.save(any(ProductImage.class)))
                .thenAnswer(invocation -> {

                    ProductImage image =
                            invocation.getArgument(0);

                    image.setId(10L);

                    return image;
                });

        var response =
                service.uploadImage(1L, file);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getProductId());
        assertEquals(
                "iphone.jpg",
                response.getOriginalFileName()
        );

        verify(s3Service)
                .uploadFile(
                        any(MultipartFile.class),
                        anyString()
                );

        verify(productImageRepository)
                .save(any(ProductImage.class));
    }

    @Test
    void uploadImage_shouldRejectEmptyFile() {

        Product product = Product.builder()
                .id(1L)
                .build();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "iphone.jpg",
                        "image/jpeg",
                        new byte[0]
                );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadImage(1L, file)
        );

        verifyNoInteractions(s3Service);
    }

    @Test
    void uploadImage_shouldRejectUnsupportedContentType() {

        Product product = Product.builder()
                .id(1L)
                .build();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test.txt",
                        "text/plain",
                        "hello".getBytes()
                );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadImage(1L, file)
        );

        verifyNoInteractions(s3Service);
    }

    @Test
    void uploadImage_shouldFailWhenProductDoesNotExist() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "iphone.jpg",
                        "image/jpeg",
                        "test".getBytes()
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.uploadImage(999L, file)
        );

        verifyNoInteractions(s3Service);
    }

    @Test
    void uploadImage_shouldCleanupS3WhenDatabaseSaveFails() {

        Product product = Product.builder()
                .id(1L)
                .build();

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "iphone.jpg",
                        "image/jpeg",
                        "test image".getBytes()
                );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productImageRepository.save(any(ProductImage.class)))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Database error"
                        )
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.uploadImage(1L, file)
        );

        verify(s3Service)
                .uploadFile(
                        any(MultipartFile.class),
                        anyString()
                );

        verify(s3Service)
                .deleteFile(anyString());
    }

    @Test
    void uploadImage_shouldRejectFileLargerThan5MB() {

        Product product = new Product();
        product.setId(1L);

        byte[] largeFile =
                new byte[5 * 1024 * 1024 + 1];

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "large.jpg",
                        "image/jpeg",
                        largeFile
                );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadImage(1L, file)
        );

        verifyNoInteractions(s3Service);
    }

    @Test
    void uploadImage_shouldRejectInvalidExtension() {

        Product product = new Product();
        product.setId(1L);

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "image.exe",
                        "image/jpeg",
                        "test".getBytes()
                );

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.uploadImage(1L, file)
        );

        verifyNoInteractions(s3Service);
    }

}
