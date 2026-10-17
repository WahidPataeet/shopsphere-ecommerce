package com.shopsphere.notification.kafka.dto.response;

public record KafkaDemoResponse(
        String messageId,
        String status,
        String message
) {
}
