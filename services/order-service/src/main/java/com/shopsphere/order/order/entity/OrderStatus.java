package com.shopsphere.order.order.entity;

import java.util.EnumSet;
import java.util.Set;

public enum OrderStatus {

    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    FAILED;

    public boolean canTransitionTo(OrderStatus target) {
        if (target == null || target == this) {
            return false;
        }

        Set<OrderStatus> allowedStatuses = switch (this) {
            case PENDING -> EnumSet.of(
                    CONFIRMED, CANCELLED, FAILED
            );
            case CONFIRMED -> EnumSet.of(
                    PROCESSING, CANCELLED, FAILED
            );
            case PROCESSING -> EnumSet.of(
                    SHIPPED, CANCELLED, FAILED
            );
            case SHIPPED -> EnumSet.of(DELIVERED);
            case DELIVERED, CANCELLED, FAILED ->
                    EnumSet.noneOf(OrderStatus.class);
        };

        return allowedStatuses.contains(target);
    }
}
