package com.genc.billing_service.repository;

import com.genc.billing_service.model.Invoice;
import com.genc.billing_service.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Find by invoice number
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    // Find by patient
    List<Invoice> findByPatientIdOrderByInvoiceDateDesc(Long patientId);

    // Find by appointment
    Optional<Invoice> findByAppointmentId(Long appointmentId);

    // Find by payment status
    List<Invoice> findByPaymentStatus(PaymentStatus status);

    // Find unpaid invoices
    List<Invoice> findByPaymentStatusNotOrderByInvoiceDateDesc(PaymentStatus status);

    // Find invoices by date range
    List<Invoice> findByInvoiceDateBetween(LocalDate startDate, LocalDate endDate);

    // Check if invoice number exists
    boolean existsByInvoiceNumber(String invoiceNumber);

    // Get next invoice number
    @Query("SELECT MAX(i.invoiceId) FROM Invoice i")
    Long getMaxInvoiceId();

    // Count by payment status
    Long countByPaymentStatus(PaymentStatus status);

    // Delete all invoices for a patient (cascade)
    void deleteByPatientId(Long patientId);
}

