package com.genc.billing_service.repository;

import com.genc.billing_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find payments by invoice
    List<Payment> findByInvoiceInvoiceId(Long invoiceId);

    // Find payments by date range
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find payments by method
    List<Payment> findByPaymentMethod(String paymentMethod);
}

