package com.shopsphere.payment.webhook.repository;

import com.shopsphere.payment.webhook.entity.PaymentWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentWebhookEventRepository
        extends JpaRepository<PaymentWebhookEvent, Long> {

    boolean existsByProviderAndProviderEventId(
            String provider,
            String providerEventId
    );

    Optional<PaymentWebhookEvent> findByProviderAndProviderEventId(
            String provider,
            String providerEventId
    );
}
