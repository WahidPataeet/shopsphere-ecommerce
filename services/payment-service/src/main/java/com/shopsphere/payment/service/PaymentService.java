package com.shopsphere.payment.service;

import com.shopsphere.payment.dto.CreatePaymentRequest;
import com.shopsphere.payment.dto.PaymentResponse;
import com.shopsphere.payment.entity.PaymentStatus;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request);

    PaymentResponse getPaymentById(Long paymentId);

    PaymentResponse getPaymentByReference(String paymentReference);

    List<PaymentResponse> getPaymentsByOrderId(Long orderId);

    PaymentResponse transitionStatus(
            Long paymentId,
            PaymentStatus targetStatus,
            String reason
    );
}
