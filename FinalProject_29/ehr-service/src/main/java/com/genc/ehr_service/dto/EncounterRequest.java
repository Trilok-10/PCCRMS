package com.genc.ehr_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncounterRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    private Long appointmentId;

    @NotNull(message = "Encounter date is required")
    private LocalDate encounterDate;

    private String encounterType;

    private String chiefComplaint;

    private String clinicalNotes;

    private String diagnosisCode;

    private String vitalsSummary;
}

