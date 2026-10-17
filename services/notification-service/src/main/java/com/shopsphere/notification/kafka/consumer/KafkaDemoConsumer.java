package com.shopsphere.notification.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaDemoConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaDemoConsumer.class);

    @KafkaListener(
            topics = "${shopsphere.kafka.demo-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(String message) {
        log.info("Kafka demo message received: {}", message);
    }
}
