package com.genc.billing_service.dto;

import com.genc.billing_service.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDTO {

    private Long invoiceId;
    private String invoiceNumber;
    private Long patientId;
    private Long appointmentId;
    private LocalDate invoiceDate;
    private BigDecimal totalAmount;
    private BigDecimal patientPayable;
    private BigDecimal amountPaid;
    private PaymentStatus paymentStatus;
    private String description;
    private List<InvoiceItemDTO> items;
    private List<PaymentDTO> payments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

