package com.shopsphere.payment.webhook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.payment.webhook.dto.PaymentWebhookRequest;
import com.shopsphere.payment.webhook.dto.PaymentWebhookResponse;
import com.shopsphere.payment.webhook.exception.InvalidWebhookSignatureException;
import com.shopsphere.payment.webhook.repository.PaymentWebhookEventRepository;
import com.shopsphere.payment.webhook.service.PaymentWebhookService;
import com.shopsphere.payment.webhook.service.WebhookSignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/payments/webhooks")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final ObjectMapper objectMapper;
    private final WebhookSignatureService signatureService;
    private final PaymentWebhookService webhookService;
    private final PaymentWebhookEventRepository eventRepository;

    @PostMapping(
            value = "/{provider}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> receiveWebhook(
            @PathVariable String provider,
            @RequestHeader(
                    value = "X-Webhook-Signature",
                    required = false
            ) String signature,
            @RequestBody String rawBody
    ) {
        final String normalizedProvider =
                provider.toUpperCase(Locale.ROOT);

        final PaymentWebhookRequest request;

        try {
            // Verify the exact request body before parsing it.
            signatureService.verify(rawBody, signature);

            request = objectMapper.readValue(
                    rawBody,
                    PaymentWebhookRequest.class
            );
        } catch (InvalidWebhookSignatureException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(errorBody(
                            HttpStatus.UNAUTHORIZED,
                            exception.getMessage()
                    ));
        } catch (JsonProcessingException exception) {
            return ResponseEntity.badRequest()
                    .body(errorBody(
                            HttpStatus.BAD_REQUEST,
                            "Malformed webhook JSON"
                    ));
        }

        try {
            PaymentWebhookResponse response = webhookService.process(
                    normalizedProvider,
                    rawBody,
                    request
            );

            return ResponseEntity.ok(response);

        } catch (DataIntegrityViolationException exception) {
            if (request.eventId() != null) {
                var existingEvent =
                        eventRepository.findByProviderAndProviderEventId(
                                normalizedProvider,
                                request.eventId()
                        );

                if (existingEvent.isPresent()) {
                    String incomingHash = sha256(rawBody);

                    if (existingEvent.get().getPayloadHash()
                            .equals(incomingHash)) {
                        return ResponseEntity.ok(new PaymentWebhookResponse(
                                request.eventId(),
                                "DUPLICATE",
                                "Webhook event has already been processed"
                        ));
                    }

                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(errorBody(
                                    HttpStatus.CONFLICT,
                                    "Webhook event ID was reused with a different payload"
                            ));
                }
            }

            throw exception;
        }
    }

    private String sha256(String rawBody) {
        try {
            byte[] hash = java.security.MessageDigest
                    .getInstance("SHA-256")
                    .digest(rawBody.getBytes(
                            java.nio.charset.StandardCharsets.UTF_8
                    ));

            return java.util.HexFormat.of().formatHex(hash);
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Unable to calculate webhook payload hash",
                    exception
            );
        }
    }

    private Map<String, Object> errorBody(
            HttpStatus status,
            String message
    ) {
        return Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        );
    }
}
