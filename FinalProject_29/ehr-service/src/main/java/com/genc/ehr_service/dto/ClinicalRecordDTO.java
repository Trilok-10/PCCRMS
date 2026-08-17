package com.genc.ehr_service.dto;

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
    private String diagnosisCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

