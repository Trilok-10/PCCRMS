package com.genc.pharmacy_service.dto;

import com.genc.pharmacy_service.model.DispenseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionDTO {

    private Long prescriptionId;
    private Long patientId;
    private Long doctorId;
    private Long recordId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String duration;
    private Integer quantity;
    private String instructions;
    private DispenseStatus dispenseStatus;
    private LocalDate prescriptionDate;
    private LocalDateTime dispensedAt;
    private Long dispensedBy;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

