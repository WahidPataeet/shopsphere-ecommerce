package com.shopsphere.payment.webhook.exception;

public class InvalidWebhookSignatureException extends RuntimeException {

    public InvalidWebhookSignatureException(String message) {
        super(message);
    }
}
