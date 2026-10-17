package com.shopsphere.payment.webhook.service;

import com.shopsphere.payment.entity.Payment;
import com.shopsphere.payment.entity.PaymentStatus;
import com.shopsphere.payment.exception.ResourceNotFoundException;
import com.shopsphere.payment.history.entity.PaymentStatusHistory;
import com.shopsphere.payment.history.repository.PaymentStatusHistoryRepository;
import com.shopsphere.payment.repository.PaymentRepository;
import com.shopsphere.payment.webhook.dto.PaymentWebhookRequest;
import com.shopsphere.payment.webhook.dto.PaymentWebhookResponse;
import com.shopsphere.payment.webhook.entity.PaymentWebhookEvent;
import com.shopsphere.payment.webhook.repository.PaymentWebhookEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PaymentWebhookServiceImpl implements PaymentWebhookService {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusHistoryRepository historyRepository;
    private final PaymentWebhookEventRepository eventRepository;

    @Override
    @Transactional
    public PaymentWebhookResponse process(
            String provider,
            String rawBody,
            PaymentWebhookRequest request
    ) {
        validateRequest(request);

        String normalizedProvider = provider.toUpperCase(Locale.ROOT);

        String payloadHash = sha256(rawBody);

        var existingEvent = eventRepository
                .findByProviderAndProviderEventId(
                        normalizedProvider,
                        request.eventId()
                );

        if (existingEvent.isPresent()) {
            if (!existingEvent.get().getPayloadHash().equals(payloadHash)) {
                throw new IllegalStateException(
                        "Webhook event ID was reused with a different payload"
                );
            }

            return duplicateResponse(request.eventId());
        }

        /*
         * Flush now so a concurrent duplicate can be detected before
         * we perform payment side effects.
         */
        try {
            eventRepository.saveAndFlush(new PaymentWebhookEvent(
                    normalizedProvider,
                    request.eventId(),
                    request.paymentReference(),
                    payloadHash
            ));
        } catch (DataIntegrityViolationException exception) {
            /*
             * A concurrent request may have inserted the same event.
             * Let the exception escape so this transaction rolls back.
             * The controller checks whether the unique event now exists.
             */
            throw exception;
        }

        Payment payment = paymentRepository
                .findByPaymentReference(request.paymentReference())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found: " + request.paymentReference()
                ));

        if (payment.getAmount().compareTo(request.amount()) != 0) {
            throw new IllegalArgumentException(
                    "Webhook amount does not match payment amount"
            );
        }

        if (!payment.getCurrency().equalsIgnoreCase(request.currency())) {
            throw new IllegalArgumentException(
                    "Webhook currency does not match payment currency"
            );
        }

        PaymentStatus targetStatus = parseTargetStatus(request.status());

        if (!payment.getStatus().canTransitionTo(targetStatus)) {
            /*
             * Do not let late or conflicting events overwrite a terminal
             * payment state. The transaction rolls back, including event
             * insertion, so the event can be investigated and retried.
             */
            throw new IllegalStateException(
                    "Invalid payment status transition: "
                            + payment.getStatus() + " -> " + targetStatus
            );
        }

        if (targetStatus == PaymentStatus.FAILED
                && (request.failureReason() == null
                || request.failureReason().isBlank())) {
            throw new IllegalArgumentException(
                    "Failure reason is required for a failed payment"
            );
        }

        PaymentStatus previousStatus = payment.getStatus();

        payment.changeStatus(targetStatus, request.failureReason());

        if (request.providerPaymentId() != null
                && !request.providerPaymentId().isBlank()) {
            payment.setProviderPaymentId(request.providerPaymentId());
        }

        paymentRepository.save(payment);

        historyRepository.save(new PaymentStatusHistory(
                payment.getId(),
                previousStatus,
                targetStatus,
                targetStatus == PaymentStatus.FAILED
                        ? request.failureReason()
                        : "Verified webhook event: " + request.eventId()
        ));

        return new PaymentWebhookResponse(
                request.eventId(),
                "PROCESSED",
                "Payment status updated to " + targetStatus
        );
    }

    private void validateRequest(PaymentWebhookRequest request) {
        if (request == null
                || request.eventId() == null
                || request.eventId().isBlank()
                || request.eventId().length() > 150
                || request.paymentReference() == null
                || request.paymentReference().isBlank()
                || request.paymentReference().length() > 50
                || request.amount() == null
                || request.amount().signum() <= 0
                || request.currency() == null
                || !request.currency().matches("[A-Z]{3}")
                || request.status() == null
                || request.status().isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid payment webhook payload"
            );
        }
    }

    private PaymentStatus parseTargetStatus(String status) {
        try {
            PaymentStatus target = PaymentStatus.valueOf(
                    status.toUpperCase(Locale.ROOT)
            );

            if (target != PaymentStatus.SUCCESS
                    && target != PaymentStatus.FAILED) {
                throw new IllegalArgumentException(
                        "Webhook status must be SUCCESS or FAILED"
                );
            }

            return target;
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unsupported webhook payment status: " + status,
                    exception
            );
        }
    }

    private String sha256(String rawBody) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(rawBody.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to calculate webhook payload hash",
                    exception
            );
        }
    }

    private PaymentWebhookResponse duplicateResponse(String eventId) {
        return new PaymentWebhookResponse(
                eventId,
                "DUPLICATE",
                "Webhook event has already been processed"
        );
    }
}
