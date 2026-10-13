package com.shopsphere.payment.history.repository;

import com.shopsphere.payment.history.entity.PaymentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentStatusHistoryRepository extends JpaRepository<PaymentStatusHistory, Long> {

    List<PaymentStatusHistory> findByPaymentIdOrderByCreatedAtAsc(Long paymentId);
}
