package com.shopsphere.notification.orders;

import com.shopsphere.notification.orders.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class OrderCreatedConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderCreatedConsumer.class);

    private final JsonMapper jsonMapper;

    public OrderCreatedConsumer() {
        this.jsonMapper = JsonMapper.builder().build();
    }

    @KafkaListener(
            topics = "${shopsphere.kafka.topics.orders}",
            groupId = "${shopsphere.kafka.consumer.order-group-id}",
            containerFactory = "orderEventsKafkaListenerContainerFactory"
    )
    public void consume(String message) {
        final OrderCreatedEvent event;

        try {
            event = jsonMapper.readValue(message, OrderCreatedEvent.class);
        } catch (Exception exception) {
            log.error("Could not parse ORDER_CREATED event: {}", message, exception);
            throw new IllegalArgumentException(
                    "Invalid ORDER_CREATED event JSON",
                    exception
            );
        }

        if (!"ORDER_CREATED".equals(event.eventType())
                || event.eventVersion() != 1
                || event.orderId() == null
                || event.orderNumber() == null) {
            throw new IllegalArgumentException(
                    "Unsupported or invalid ORDER_CREATED event"
            );
        }

        log.info(
                "Notification requested: orderNumber={}, userId={}, total={} {}, itemCount={}",
                event.orderNumber(),
                event.userId(),
                event.totalAmount(),
                event.currency(),
                event.itemCount()
        );
    }
}
