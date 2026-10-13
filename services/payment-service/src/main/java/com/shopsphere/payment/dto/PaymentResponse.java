package com.shopsphere.payment.dto;

import com.shopsphere.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        String paymentReference,
        Long orderId,
        Long userId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String provider,
        String providerPaymentId,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
