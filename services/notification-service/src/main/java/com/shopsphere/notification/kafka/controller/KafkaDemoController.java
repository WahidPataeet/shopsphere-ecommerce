package com.shopsphere.notification.kafka.controller;

import com.shopsphere.notification.kafka.dto.request.KafkaDemoRequest;
import com.shopsphere.notification.kafka.dto.response.KafkaDemoResponse;
import com.shopsphere.notification.kafka.producer.KafkaDemoProducer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/kafka-demo/messages")
public class KafkaDemoController {

    private final KafkaDemoProducer producer;

    public KafkaDemoController(KafkaDemoProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<KafkaDemoResponse>> publish(
            @Valid @RequestBody KafkaDemoRequest request
    ) {
        String messageId = UUID.randomUUID().toString();

        return producer.publish(messageId, request.message())
                .thenApply(result -> ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .body(new KafkaDemoResponse(
                                messageId,
                                "PUBLISHED",
                                "Kafka acknowledged the message"
                        )));
    }
}
