package com.genc.appointment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Local DTO copy mirroring billing-service's InvoiceRequest.
 * Used to construct Feign client requests to billing-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceRequest {

    private Long patientId;
    private Long appointmentId;
    private String description;
    private String insuranceProvider;
    private String policyNumber;
    private List<InvoiceItemRequest> items;
}
