package com.genc.billing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {

    private Long paymentId;
    private Long invoiceId;
    private BigDecimal amount;
    private String paymentMethod;
    private String transactionReference;
    private String notes;
    private Long receivedBy;
    private LocalDateTime paymentDate;
}

