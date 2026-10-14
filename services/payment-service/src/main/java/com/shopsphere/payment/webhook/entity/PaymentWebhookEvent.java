package com.shopsphere.payment.webhook.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "payment_webhook_events",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_webhook_provider_event",
                columnNames = {"provider", "provider_event_id"}
        )
)
@Getter
@NoArgsConstructor
public class PaymentWebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String provider;

    @Column(name = "provider_event_id", nullable = false, length = 150)
    private String providerEventId;

    @Column(name = "payment_reference", nullable = false, length = 50)
    private String paymentReference;

    @Column(name = "payload_hash", nullable = false, length = 64)
    private String payloadHash;

    @Column(name = "processing_status", nullable = false, length = 20)
    private String processingStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PaymentWebhookEvent(
            String provider,
            String providerEventId,
            String paymentReference,
            String payloadHash
    ) {
        this.provider = provider;
        this.providerEventId = providerEventId;
        this.paymentReference = paymentReference;
        this.payloadHash = payloadHash;
        this.processingStatus = "PROCESSED";
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
