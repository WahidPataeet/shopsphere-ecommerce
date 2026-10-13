package com.shopsphere.payment.controller;

import com.shopsphere.payment.dto.CreatePaymentRequest;
import com.shopsphere.payment.dto.PaymentResponse;
import com.shopsphere.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        PaymentResponse response =
                paymentService.createPayment(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long paymentId
    ) {
        return ResponseEntity.ok(
                paymentService.getPaymentById(paymentId)
        );
    }

    @GetMapping("/reference/{paymentReference}")
    public ResponseEntity<PaymentResponse> getPaymentByReference(
            @PathVariable String paymentReference
    ) {
        return ResponseEntity.ok(
                paymentService.getPaymentByReference(paymentReference)
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrderId(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(
                paymentService.getPaymentsByOrderId(orderId)
        );
    }
}