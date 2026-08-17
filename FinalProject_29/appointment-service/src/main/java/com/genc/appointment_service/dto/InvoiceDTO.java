package com.genc.appointment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Local DTO copy mirroring billing-service's InvoiceDTO.
 * Used to deserialize Feign client responses from billing-service.
 */
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
    private BigDecimal insuranceCoverage;
    private BigDecimal patientPayable;
    private BigDecimal amountPaid;
    private String paymentStatus;
    private String claimStatus;
    private String insuranceProvider;
    private String policyNumber;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
