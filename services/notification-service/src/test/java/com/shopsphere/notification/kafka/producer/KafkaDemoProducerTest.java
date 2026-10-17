package com.shopsphere.notification.kafka.producer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaDemoProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void publishShouldSendMessageWithKeyToConfiguredTopic() {
        KafkaDemoProducer producer = new KafkaDemoProducer(
                kafkaTemplate,
                "shopsphere.day19.demo"
        );

        String messageId = "message-123";
        String message = "Hello from ShopSphere";

        CompletableFuture<SendResult<String, String>> future =
                new CompletableFuture<>();

        when(kafkaTemplate.send(
                "shopsphere.day19.demo",
                messageId,
                message
        )).thenReturn(future);

        CompletableFuture<SendResult<String, String>> result =
                producer.publish(messageId, message);

        assertSame(future, result);

        verify(kafkaTemplate).send(
                "shopsphere.day19.demo",
                messageId,
                message
        );
    }
}
