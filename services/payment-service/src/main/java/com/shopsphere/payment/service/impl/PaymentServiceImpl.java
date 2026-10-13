package com.shopsphere.payment.service.impl;

import com.shopsphere.payment.dto.CreatePaymentRequest;
import com.shopsphere.payment.dto.PaymentResponse;
import com.shopsphere.payment.entity.Payment;
import com.shopsphere.payment.entity.PaymentStatus;
import com.shopsphere.payment.exception.ResourceNotFoundException;
import com.shopsphere.payment.history.entity.PaymentStatusHistory;
import com.shopsphere.payment.history.repository.PaymentStatusHistoryRepository;
import com.shopsphere.payment.repository.PaymentRepository;
import com.shopsphere.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusHistoryRepository historyRepository;

    @Override
    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        Payment payment = new Payment(
                request.orderId(),
                request.userId(),
                request.amount(),
                request.currency()
        );

        Payment savedPayment = paymentRepository.save(payment);

        historyRepository.save(
                new PaymentStatusHistory(
                        savedPayment.getId(),
                        null,
                        PaymentStatus.PENDING,
                        "Payment attempt created"
                )
        );

        return toResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: " + paymentId
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByReference(String paymentReference) {
        return paymentRepository.findByPaymentReference(paymentReference)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with reference: "
                                        + paymentReference
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        return paymentRepository
                .findByOrderIdOrderByCreatedAtDesc(orderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PaymentResponse transitionStatus(
            Long paymentId,
            PaymentStatus targetStatus,
            String reason
    ) {
        if (targetStatus == null) {
            throw new IllegalArgumentException(
                    "Target payment status is required"
            );
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found with id: " + paymentId
                        )
                );

        PaymentStatus currentStatus = payment.getStatus();

        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new IllegalStateException(
                    "Cannot transition payment from "
                            + currentStatus + " to " + targetStatus
            );
        }

        if (targetStatus == PaymentStatus.FAILED
                && (reason == null || reason.isBlank())) {
            throw new IllegalArgumentException(
                    "A reason is required for a failed payment"
            );
        }

        payment.changeStatus(targetStatus, reason);

        historyRepository.save(
                new PaymentStatusHistory(
                        payment.getId(),
                        currentStatus,
                        targetStatus,
                        reason
                )
        );

        return toResponse(paymentRepository.save(payment));
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentReference(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getProvider(),
                payment.getProviderPaymentId(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
