package com.shopsphere.payment.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentStatusTest {

    @Test
    void pendingCanBecomeSuccess() {
        assertTrue(
                PaymentStatus.PENDING.canTransitionTo(
                        PaymentStatus.SUCCESS
                )
        );
    }

    @Test
    void pendingCanBecomeFailed() {
        assertTrue(
                PaymentStatus.PENDING.canTransitionTo(
                        PaymentStatus.FAILED
                )
        );
    }

    @Test
    void pendingCanBecomeCancelled() {
        assertTrue(
                PaymentStatus.PENDING.canTransitionTo(
                        PaymentStatus.CANCELLED
                )
        );
    }

    @Test
    void successfulPaymentIsTerminal() {
        assertFalse(
                PaymentStatus.SUCCESS.canTransitionTo(
                        PaymentStatus.FAILED
                )
        );
    }

    @Test
    void failedPaymentCannotReturnToPending() {
        assertFalse(
                PaymentStatus.FAILED.canTransitionTo(
                        PaymentStatus.PENDING
                )
        );
    }

    @Test
    void sameStatusIsNotAValidTransition() {
        assertFalse(
                PaymentStatus.PENDING.canTransitionTo(
                        PaymentStatus.PENDING
                )
        );
    }
}
