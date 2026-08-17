package com.genc.pharmacy_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationDTO {

    private Long medicationId;
    private String name;
    private String genericName;
    private String manufacturer;
    private String category;
    private String dosageForm;
    private String strength;
    private Integer stockQuantity;
    private Integer reorderLevel;
    private BigDecimal unitPrice;
    private LocalDate expiryDate;
    private String contraindications;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

