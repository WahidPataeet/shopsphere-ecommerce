package com.shopsphere.notification.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaDemoProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topicName;

    public KafkaDemoProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${shopsphere.kafka.demo-topic}") String topicName
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public CompletableFuture<SendResult<String, String>> publish(
            String messageId,
            String message
    ) {
        return kafkaTemplate.send(topicName, messageId, message);
    }
}
