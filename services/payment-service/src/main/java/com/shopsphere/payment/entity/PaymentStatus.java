package com.shopsphere.payment.entity;

import java.util.EnumSet;
import java.util.Set;

public enum PaymentStatus {

    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED;

    public boolean canTransitionTo(PaymentStatus target) {
        if (target == null || target == this) {
            return false;
        }

        Set<PaymentStatus> allowed = switch (this) {
            case PENDING -> EnumSet.of(
                    SUCCESS, FAILED, CANCELLED
            );
            case SUCCESS, FAILED, CANCELLED ->
                    EnumSet.noneOf(PaymentStatus.class);
        };

        return allowed.contains(target);
    }
}
