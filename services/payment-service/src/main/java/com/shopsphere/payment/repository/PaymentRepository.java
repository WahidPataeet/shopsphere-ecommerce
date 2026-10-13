package com.shopsphere.payment.repository;

import com.shopsphere.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByPaymentReference(String paymentReference);

    List<Payment> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);
}
