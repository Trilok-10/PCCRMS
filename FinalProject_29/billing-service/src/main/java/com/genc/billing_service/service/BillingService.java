package com.genc.billing_service.service;

import com.genc.billing_service.dto.*;
import com.genc.billing_service.model.*;
import com.genc.billing_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    // ==================== INVOICE OPERATIONS ====================

    // Generate invoice
    @Transactional
    public InvoiceDTO generateInvoice(InvoiceRequest request) {
        log.info("Generating invoice for patient {}", request.getPatientId());

        String invoiceNumber = generateInvoiceNumber();

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .patientId(request.getPatientId())
                .appointmentId(request.getAppointmentId())
                .invoiceDate(LocalDate.now())
                .description(request.getDescription())
                .paymentStatus(PaymentStatus.UNPAID)
                .amountPaid(BigDecimal.ZERO)
                .build();

        // Add invoice items
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (request.getItems() != null) {
            for (InvoiceItemRequest itemRequest : request.getItems()) {
                BigDecimal itemAmount = itemRequest.getUnitPrice()
                        .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

                InvoiceItem item = InvoiceItem.builder()
                        .invoice(invoice)
                        .serviceName(itemRequest.getServiceName())
                        .serviceCode(itemRequest.getServiceCode())
                        .quantity(itemRequest.getQuantity())
                        .unitPrice(itemRequest.getUnitPrice())
                        .amount(itemAmount)
                        .description(itemRequest.getDescription())
                        .build();

                invoice.getItems().add(item);
                totalAmount = totalAmount.add(itemAmount);
            }
        }

        invoice.setTotalAmount(totalAmount);
        invoice.setPatientPayable(totalAmount); // Initially, patient pays full amount

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice generated with number: {}", invoiceNumber);

        return mapToDTO(saved);
    }

    private String generateInvoiceNumber() {
        String prefix = "INV";
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", new Random().nextInt(10000));
        String invoiceNumber = prefix + date + random;

        while (invoiceRepository.existsByInvoiceNumber(invoiceNumber)) {
            random = String.format("%04d", new Random().nextInt(10000));
            invoiceNumber = prefix + date + random;
        }

        return invoiceNumber;
    }

    // Get invoice by ID
    public InvoiceDTO getInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return mapToDTO(invoice);
    }

    // Get invoice by number
    public InvoiceDTO getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return mapToDTO(invoice);
    }

    // Get patient invoices
    public List<InvoiceDTO> getPatientInvoices(Long patientId) {
        return invoiceRepository.findByPatientIdOrderByInvoiceDateDesc(patientId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get all invoices
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get unpaid invoices
    public List<InvoiceDTO> getUnpaidInvoices() {
        return invoiceRepository.findByPaymentStatusNotOrderByInvoiceDateDesc(PaymentStatus.PAID)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Delete invoice
    @Transactional
    public void deleteInvoice(Long invoiceId) {
        log.info("Deleting invoice with ID: {}", invoiceId);
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        // Check if invoice has any payments
        if (invoice.getAmountPaid() != null && invoice.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Cannot delete invoice with existing payments");
        }
        
        invoiceRepository.delete(invoice);
        log.info("Invoice deleted successfully: {}", invoice.getInvoiceNumber());
    }

    // ==================== PAYMENT OPERATIONS ====================

    // Record payment
    @Transactional
    public PaymentDTO recordPayment(PaymentRequest request) {
        log.info("Recording payment for invoice {}", request.getInvoiceId());

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Invoice is already fully paid");
        }

        BigDecimal remainingAmount = invoice.getPatientPayable().subtract(invoice.getAmountPaid());
        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new RuntimeException("Payment amount exceeds remaining balance");
        }

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionReference(request.getTransactionReference())
                .notes(request.getNotes())
                .receivedBy(request.getReceivedBy())
                .build();

        Payment saved = paymentRepository.save(payment);

        // Update invoice
        BigDecimal newAmountPaid = invoice.getAmountPaid().add(request.getAmount());
        invoice.setAmountPaid(newAmountPaid);

        if (newAmountPaid.compareTo(invoice.getPatientPayable()) >= 0) {
            invoice.setPaymentStatus(PaymentStatus.PAID);
        } else {
            invoice.setPaymentStatus(PaymentStatus.PARTIAL);
        }

        invoiceRepository.save(invoice);
        log.info("Payment recorded successfully");

        return mapPaymentToDTO(saved);
    }

    // Get payments for invoice
    public List<PaymentDTO> getInvoicePayments(Long invoiceId) {
        return paymentRepository.findByInvoiceInvoiceId(invoiceId)
                .stream()
                .map(this::mapPaymentToDTO)
                .collect(Collectors.toList());
    }

    // ==================== MAPPING METHODS ====================

    private InvoiceDTO mapToDTO(Invoice invoice) {
        List<InvoiceItemDTO> items = invoice.getItems() != null ?
                invoice.getItems().stream().map(this::mapItemToDTO).collect(Collectors.toList()) :
                List.of();

        List<PaymentDTO> payments = invoice.getPayments() != null ?
                invoice.getPayments().stream().map(this::mapPaymentToDTO).collect(Collectors.toList()) :
                List.of();

        return InvoiceDTO.builder()
                .invoiceId(invoice.getInvoiceId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .patientId(invoice.getPatientId())
                .appointmentId(invoice.getAppointmentId())
                .invoiceDate(invoice.getInvoiceDate())
                .totalAmount(invoice.getTotalAmount())
                .patientPayable(invoice.getPatientPayable())
                .amountPaid(invoice.getAmountPaid())
                .paymentStatus(invoice.getPaymentStatus())
                .description(invoice.getDescription())
                .items(items)
                .payments(payments)
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }

    private InvoiceItemDTO mapItemToDTO(InvoiceItem item) {
        return InvoiceItemDTO.builder()
                .itemId(item.getItemId())
                .serviceName(item.getServiceName())
                .serviceCode(item.getServiceCode())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .amount(item.getAmount())
                .description(item.getDescription())
                .build();
    }

    private PaymentDTO mapPaymentToDTO(Payment payment) {
        return PaymentDTO.builder()
                .paymentId(payment.getPaymentId())
                .invoiceId(payment.getInvoice().getInvoiceId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionReference(payment.getTransactionReference())
                .notes(payment.getNotes())
                .receivedBy(payment.getReceivedBy())
                .paymentDate(payment.getPaymentDate())
                .build();
    }

    // Delete all invoices for a patient (cascade deletion)
    @Transactional
    public void deleteByPatientId(Long patientId) {
        log.info("Cascade deleting all invoices for patient ID: {}", patientId);
        invoiceRepository.deleteByPatientId(patientId);
    }

}

