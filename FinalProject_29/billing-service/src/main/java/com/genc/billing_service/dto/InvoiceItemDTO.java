package com.genc.billing_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemDTO {

    private Long itemId;
    private String serviceName;
    private String serviceCode;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String description;
}

