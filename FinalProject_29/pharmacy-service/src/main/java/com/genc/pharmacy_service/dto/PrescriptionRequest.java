package com.genc.pharmacy_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    private Long recordId;

    @NotBlank(message = "Medication name is required")
    private String medicationName;

    private String dosage;

    private String frequency;

    private String duration;

    private Integer quantity;

    private String instructions;

    private String notes;
}

