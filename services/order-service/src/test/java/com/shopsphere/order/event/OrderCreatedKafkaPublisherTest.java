package com.shopsphere.order.event;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderCreatedKafkaPublisherTest {

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate =
            mock(KafkaTemplate.class);

    @Test
    void publishesEventUsingOrderIdAsKey() {
        String topic = "shopsphere.orders.events.v1";

        OrderCreatedKafkaPublisher publisher =
                new OrderCreatedKafkaPublisher(kafkaTemplate, topic);

        OrderCreatedEvent event = new OrderCreatedEvent(
                "event-123",
                "ORDER_CREATED",
                1,
                Instant.parse("2026-10-19T06:00:00Z"),
                101L,
                "SS-example",
                25L,
                "INR",
                new BigDecimal("2499.00"),
                2
        );

        when(kafkaTemplate.send(topic, "101", event))
                .thenReturn(CompletableFuture.completedFuture(null));

        publisher.publish(event);

        verify(kafkaTemplate).send(topic, "101", event);
    }
}
