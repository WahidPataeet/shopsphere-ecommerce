package com.shopsphere.order.event;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderCreatedEvent(
        String eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        Long orderId,
        String orderNumber,
        Long userId,
        String currency,
        BigDecimal totalAmount,
        int itemCount
) {
}
