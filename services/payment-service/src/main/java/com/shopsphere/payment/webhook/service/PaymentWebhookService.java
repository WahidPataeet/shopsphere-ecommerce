package com.shopsphere.payment.webhook.service;

import com.shopsphere.payment.webhook.dto.PaymentWebhookRequest;
import com.shopsphere.payment.webhook.dto.PaymentWebhookResponse;

public interface PaymentWebhookService {

    PaymentWebhookResponse process(
            String provider,
            String rawBody,
            PaymentWebhookRequest request
    );
}
