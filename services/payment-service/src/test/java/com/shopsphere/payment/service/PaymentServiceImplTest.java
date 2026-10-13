package com.shopsphere.payment.service;

import com.shopsphere.payment.dto.CreatePaymentRequest;
import com.shopsphere.payment.dto.PaymentResponse;
import com.shopsphere.payment.entity.Payment;
import com.shopsphere.payment.entity.PaymentStatus;
import com.shopsphere.payment.exception.ResourceNotFoundException;
import com.shopsphere.payment.history.entity.PaymentStatusHistory;
import com.shopsphere.payment.history.repository.PaymentStatusHistoryRepository;
import com.shopsphere.payment.repository.PaymentRepository;

import com.shopsphere.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentStatusHistoryRepository historyRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    // Test 1: Create a payment and record initial history
    @Test
    void createPaymentShouldSavePendingPaymentAndHistory() {
        CreatePaymentRequest request = new CreatePaymentRequest(
                1L,
                10L,
                new BigDecimal("1499.00"),
                "INR"
        );

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(historyRepository.save(any(PaymentStatusHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.createPayment(request);

        assertNotNull(response);
        assertNotNull(response.paymentReference());
        assertTrue(response.paymentReference().startsWith("PAY-"));
        assertEquals(1L, response.orderId());
        assertEquals(10L, response.userId());
        assertEquals(new BigDecimal("1499.00"), response.amount());
        assertEquals("INR", response.currency());
        assertEquals(PaymentStatus.PENDING, response.status());
        assertEquals("MANUAL", response.provider());

        ArgumentCaptor<PaymentStatusHistory> historyCaptor =
                ArgumentCaptor.forClass(PaymentStatusHistory.class);

        verify(paymentRepository).save(any(Payment.class));
        verify(historyRepository).save(historyCaptor.capture());

        PaymentStatusHistory history = historyCaptor.getValue();

        assertNull(history.getPreviousStatus());
        assertEquals(PaymentStatus.PENDING, history.getNewStatus());
        assertEquals("Payment attempt created", history.getReason());
    }

    // Test 2: Missing payment should throw an exception
    @Test
    void getPaymentByIdShouldThrowWhenPaymentDoesNotExist() {
        when(paymentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> paymentService.getPaymentById(99L)
        );

        verify(paymentRepository).findById(99L);
    }

    // Test 3: PENDING -> SUCCESS should update status and history
    @Test
    void transitionStatusShouldUpdatePaymentAndSaveHistory() {
        Payment payment = createPaymentWithId(1L);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(historyRepository.save(any(PaymentStatusHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.transitionStatus(
                1L,
                PaymentStatus.SUCCESS,
                "Payment confirmed"
        );

        assertEquals(PaymentStatus.SUCCESS, response.status());

        ArgumentCaptor<PaymentStatusHistory> historyCaptor =
                ArgumentCaptor.forClass(PaymentStatusHistory.class);

        verify(historyRepository).save(historyCaptor.capture());

        PaymentStatusHistory history = historyCaptor.getValue();

        assertEquals(1L, history.getPaymentId());
        assertEquals(PaymentStatus.PENDING, history.getPreviousStatus());
        assertEquals(PaymentStatus.SUCCESS, history.getNewStatus());
        assertEquals("Payment confirmed", history.getReason());

        verify(paymentRepository).save(payment);
    }

    // Test 4: A completed payment cannot transition to FAILED
    @Test
    void transitionStatusShouldRejectInvalidTransition() {
        Payment payment = createPaymentWithId(1L);
        payment.changeStatus(PaymentStatus.SUCCESS, null);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        assertThrows(
                IllegalStateException.class,
                () -> paymentService.transitionStatus(
                        1L,
                        PaymentStatus.FAILED,
                        "Payment failed"
                )
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(historyRepository);
    }

    // Test 5: FAILED status requires a reason
    @Test
    void transitionToFailedShouldRequireReason() {
        Payment payment = createPaymentWithId(1L);

        when(paymentRepository.findById(1L))
                .thenReturn(Optional.of(payment));

        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.transitionStatus(
                        1L,
                        PaymentStatus.FAILED,
                        " "
                )
        );

        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(historyRepository);
    }

    // Test 6: A payment must exist before its status can change
    @Test
    void transitionStatusShouldThrowWhenPaymentDoesNotExist() {
        when(paymentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> paymentService.transitionStatus(
                        99L,
                        PaymentStatus.SUCCESS,
                        "Payment confirmed"
                )
        );

        verifyNoInteractions(historyRepository);
    }

    private Payment createPaymentWithId(Long id) {
        Payment payment = new Payment(
                1L,
                10L,
                new BigDecimal("1499.00"),
                "INR"
        );

        // Simulate the database-generated ID in a unit test.
        ReflectionTestUtils.setField(payment, "id", id);

        return payment;
    }
}
