package com.shopsphere.payment.webhook.dto;

public record PaymentWebhookResponse(
        String eventId,
        String result,
        String message
) {
}
