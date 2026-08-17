package com.genc.pharmacy_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationRequest {

    @NotBlank(message = "Medication name is required")
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
}

