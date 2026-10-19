package com.shopsphere.order.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderCreatedKafkaPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OrderCreatedKafkaPublisher.class);

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
    private final String topicName;

    public OrderCreatedKafkaPublisher(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate,
            @Value("${shopsphere.kafka.topics.orders}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(OrderCreatedEvent event) {
        String key = event.orderId().toString();

        kafkaTemplate.send(topicName, key, event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error(
                                "Failed to publish ORDER_CREATED for orderId={}",
                                event.orderId(),
                                exception
                        );
                        return;
                    }

                    log.info(
                            "Published ORDER_CREATED: orderId={}, topic={}, partition={}",
                            event.orderId(),
                            topicName,
                            result.getRecordMetadata().partition()
                    );
                });
    }
}
