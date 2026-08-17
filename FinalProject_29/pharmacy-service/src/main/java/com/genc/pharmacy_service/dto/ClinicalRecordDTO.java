package com.genc.pharmacy_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Local DTO copy mirroring ehr-service's ClinicalRecordDTO.
 * Used to deserialize Feign client responses from ehr-service.
 * Only includes fields needed for the pharmacy→EHR workflow.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalRecordDTO {

    private Long recordId;
    private Long patientId;
    private Long doctorId;
    private Long appointmentId;
    private LocalDate encounterDate;
    private String encounterType;
    private String chiefComplaint;
    private String clinicalNotes;
    private String vitalsSummary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
