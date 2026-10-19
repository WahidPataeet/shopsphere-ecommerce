package com.shopsphere.notification.orders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderCreatedConsumerTest {

    private final OrderCreatedConsumer consumer = new OrderCreatedConsumer();

    @Test
    void consumesValidOrderCreatedJson() {
        String json = """
                {
                  "eventId": "event-123",
                  "eventType": "ORDER_CREATED",
                  "eventVersion": 1,
                  "occurredAt": "2026-10-19T06:00:00Z",
                  "orderId": 101,
                  "orderNumber": "SS-example",
                  "userId": 25,
                  "currency": "INR",
                  "totalAmount": 2499.00,
                  "itemCount": 2
                }
                """;

        assertDoesNotThrow(() -> consumer.consume(json));
    }

    @Test
    void rejectsMalformedJson() {
        assertThrows(
                IllegalArgumentException.class,
                () -> consumer.consume("{ invalid json")
        );
    }

    @Test
    void rejectsUnsupportedEventVersion() {
        String json = """
                {
                  "eventId": "event-123",
                  "eventType": "ORDER_CREATED",
                  "eventVersion": 2,
                  "occurredAt": "2026-10-19T06:00:00Z",
                  "orderId": 101,
                  "orderNumber": "SS-example",
                  "userId": 25,
                  "currency": "INR",
                  "totalAmount": 2499.00,
                  "itemCount": 2
                }
                """;

        assertThrows(
                IllegalArgumentException.class,
                () -> consumer.consume(json)
        );
    }
}
