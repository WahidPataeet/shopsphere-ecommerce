
package com.shopsphere.payment.webhook.service;

import com.shopsphere.payment.entity.Payment;
import com.shopsphere.payment.entity.PaymentStatus;
import com.shopsphere.payment.exception.ResourceNotFoundException;
import com.shopsphere.payment.history.repository.PaymentStatusHistoryRepository;
import com.shopsphere.payment.repository.PaymentRepository;
import com.shopsphere.payment.webhook.dto.PaymentWebhookRequest;
import com.shopsphere.payment.webhook.dto.PaymentWebhookResponse;
import com.shopsphere.payment.webhook.entity.PaymentWebhookEvent;
import com.shopsphere.payment.webhook.repository.PaymentWebhookEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentWebhookServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentStatusHistoryRepository historyRepository;

    @Mock
    private PaymentWebhookEventRepository eventRepository;

    @InjectMocks
    private PaymentWebhookServiceImpl webhookService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment(
                100L,
                10L,
                new BigDecimal("1499.00"),
                "INR"
        );

        ReflectionTestUtils.setField(payment, "id", 1L);
        ReflectionTestUtils.setField(
                payment, "paymentReference", "PAY-TEST-001"
        );
    }

    @Test
    void processShouldMarkPaymentSuccessfulAndSaveHistory() {
        String body = "{\"eventId\":\"evt-001\"}";

        PaymentWebhookRequest request = new PaymentWebhookRequest(
                "evt-001",
                "PAY-TEST-001",
                "provider-pay-001",
                new BigDecimal("1499.00"),
                "INR",
                "SUCCESS",
                null
        );

        when(eventRepository.findByProviderAndProviderEventId(
                "MANUAL", "evt-001"
        )).thenReturn(Optional.empty());

        when(eventRepository.saveAndFlush(any(PaymentWebhookEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(paymentRepository.findByPaymentReference("PAY-TEST-001"))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(historyRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentWebhookResponse response = webhookService.process(
                "MANUAL", body, request
        );

        assertEquals("PROCESSED", response.result());
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals(
                "provider-pay-001",
                payment.getProviderPaymentId()
        );

        verify(paymentRepository).save(payment);
        verify(historyRepository).save(any());
        verify(eventRepository).saveAndFlush(any(PaymentWebhookEvent.class));
    }

    @Test
    void processShouldAcknowledgeSameEventAndPayloadAsDuplicate()
            throws Exception {

        String body = "{\"eventId\":\"evt-duplicate\"}";

        PaymentWebhookRequest request = new PaymentWebhookRequest(
                "evt-duplicate",
                "PAY-TEST-001",
                "provider-pay-001",
                new BigDecimal("1499.00"),
                "INR",
                "SUCCESS",
                null
        );

        String payloadHash = HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(
                        body.getBytes(StandardCharsets.UTF_8)
                )
        );

        PaymentWebhookEvent existingEvent = new PaymentWebhookEvent(
                "MANUAL",
                "evt-duplicate",
                "PAY-TEST-001",
                payloadHash
        );

        when(eventRepository.findByProviderAndProviderEventId(
                "MANUAL", "evt-duplicate"
        )).thenReturn(Optional.of(existingEvent));

        PaymentWebhookResponse response = webhookService.process(
                "MANUAL", body, request
        );

        assertEquals("DUPLICATE", response.result());
        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(historyRepository);
        verify(eventRepository, never())
                .saveAndFlush(any(PaymentWebhookEvent.class));
    }

    @Test
    void processShouldRejectReusedEventIdWithDifferentPayload() {
        String body = "{\"eventId\":\"evt-reused\",\"status\":\"FAILED\"}";

        PaymentWebhookRequest request = new PaymentWebhookRequest(
                "evt-reused",
                "PAY-TEST-001",
                "provider-pay-001",
                new BigDecimal("1499.00"),
                "INR",
                "FAILED",
                "Provider reported failure"
        );

        PaymentWebhookEvent existingEvent = new PaymentWebhookEvent(
                "MANUAL",
                "evt-reused",
                "PAY-TEST-001",
                "0".repeat(64)
        );

        when(eventRepository.findByProviderAndProviderEventId(
                "MANUAL", "evt-reused"
        )).thenReturn(Optional.of(existingEvent));

        assertThrows(
                IllegalStateException.class,
                () -> webhookService.process("MANUAL", body, request)
        );

        verifyNoInteractions(paymentRepository);
        verifyNoInteractions(historyRepository);
    }

    @Test
    void processShouldRejectMismatchedAmount() {
        String body = "{\"eventId\":\"evt-amount\"}";

        PaymentWebhookRequest request = new PaymentWebhookRequest(
                "evt-amount",
                "PAY-TEST-001",
                "provider-pay-002",
                new BigDecimal("1.00"),
                "INR",
                "SUCCESS",
                null
        );

        when(eventRepository.findByProviderAndProviderEventId(
                "MANUAL", "evt-amount"
        )).thenReturn(Optional.empty());

        when(eventRepository.saveAndFlush(any(PaymentWebhookEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(paymentRepository.findByPaymentReference("PAY-TEST-001"))
                .thenReturn(Optional.of(payment));

        assertThrows(
                IllegalArgumentException.class,
                () -> webhookService.process("MANUAL", body, request)
        );

        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(historyRepository);
    }

    @Test
    void processShouldRejectUnknownPayment() {
        String body = "{\"eventId\":\"evt-missing\"}";

        PaymentWebhookRequest request = new PaymentWebhookRequest(
                "evt-missing",
                "PAY-DOES-NOT-EXIST",
                "provider-pay-003",
                new BigDecimal("1499.00"),
                "INR",
                "SUCCESS",
                null
        );

        when(eventRepository.findByProviderAndProviderEventId(
                "MANUAL", "evt-missing"
        )).thenReturn(Optional.empty());

        when(eventRepository.saveAndFlush(any(PaymentWebhookEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(paymentRepository.findByPaymentReference(
                "PAY-DOES-NOT-EXIST"
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> webhookService.process("MANUAL", body, request)
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(historyRepository);
    }
}
