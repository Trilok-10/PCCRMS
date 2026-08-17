package com.genc.billing_service.controller;

import com.genc.billing_service.dto.*;
import com.genc.billing_service.service.BillingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@Slf4j
public class BillingController {

    private final BillingService billingService;

    // ==================== INVOICE ENDPOINTS ====================

    // Generate invoice
    @PostMapping("/invoices")
    public ResponseEntity<ApiResponse<InvoiceDTO>> generateInvoice(
            @Valid @RequestBody InvoiceRequest request) {
        InvoiceDTO invoice = billingService.generateInvoice(request);
        return ResponseEntity.ok(ApiResponse.success("Invoice generated successfully", invoice));
    }

    // Get invoice by ID
    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceById(@PathVariable Long invoiceId) {
        InvoiceDTO invoice = billingService.getInvoiceById(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }

    // Get invoice by number
    @GetMapping("/invoices/number/{invoiceNumber}")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceByNumber(
            @PathVariable String invoiceNumber) {
        InvoiceDTO invoice = billingService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(ApiResponse.success(invoice));
    }

    // Get patient invoices
    @GetMapping("/invoices/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getPatientInvoices(
            @PathVariable Long patientId) {
        List<InvoiceDTO> invoices = billingService.getPatientInvoices(patientId);
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    // Get all invoices
    @GetMapping("/invoices")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getAllInvoices() {
        List<InvoiceDTO> invoices = billingService.getAllInvoices();
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    // Get unpaid invoices
    @GetMapping("/invoices/unpaid")
    public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getUnpaidInvoices() {
        List<InvoiceDTO> invoices = billingService.getUnpaidInvoices();
        return ResponseEntity.ok(ApiResponse.success(invoices));
    }

    // Delete invoice
    @DeleteMapping("/invoices/{invoiceId}")
    public ResponseEntity<ApiResponse<String>> deleteInvoice(@PathVariable Long invoiceId) {
        billingService.deleteInvoice(invoiceId);
        return ResponseEntity.ok(ApiResponse.success("Invoice deleted successfully", null));
    }

    // ==================== PAYMENT ENDPOINTS ====================

    // Record payment
    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<PaymentDTO>> recordPayment(
            @Valid @RequestBody PaymentRequest request) {
        PaymentDTO payment = billingService.recordPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment recorded successfully", payment));
    }

    // Get invoice payments
    @GetMapping("/invoices/{invoiceId}/payments")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getInvoicePayments(
            @PathVariable Long invoiceId) {
        List<PaymentDTO> payments = billingService.getInvoicePayments(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    // Cascade delete: Delete all invoices for a patient
    @DeleteMapping("/invoices/patient/{patientId}")
    public ResponseEntity<ApiResponse<String>> deleteByPatientId(@PathVariable Long patientId) {
        billingService.deleteByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient invoices deleted", null));
    }

}

