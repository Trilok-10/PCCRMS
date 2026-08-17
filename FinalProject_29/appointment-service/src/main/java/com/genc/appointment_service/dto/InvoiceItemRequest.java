package com.genc.appointment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Local DTO copy mirroring billing-service's InvoiceItemRequest.
 * Used to construct Feign client requests to billing-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemRequest {

    private String serviceName;
    private String serviceCode;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String description;
}
