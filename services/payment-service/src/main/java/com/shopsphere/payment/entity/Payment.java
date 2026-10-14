
package com.shopsphere.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_reference", nullable = false,
            unique = true, length = 50)
    private String paymentReference;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false, length = 50)
    private String provider;

    @Setter
    @Column(name = "provider_payment_id", length = 150)
    private String providerPaymentId;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Payment(
            Long orderId,
            Long userId,
            BigDecimal amount,
            String currency
    ) {
        this.paymentReference = "PAY-" + UUID.randomUUID();
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.status = PaymentStatus.PENDING;
        this.provider = "MANUAL";
    }

    public void changeStatus(
            PaymentStatus newStatus,
            String failureReason
    ) {
        this.status = newStatus;
        this.failureReason = failureReason;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
