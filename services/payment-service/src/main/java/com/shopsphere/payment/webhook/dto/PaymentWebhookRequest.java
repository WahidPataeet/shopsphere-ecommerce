package com.shopsphere.payment.webhook.dto;

import java.math.BigDecimal;

public record PaymentWebhookRequest(
        String eventId,
        String paymentReference,
        String providerPaymentId,
        BigDecimal amount,
        String currency,
        String status,
        String failureReason
) {
}
