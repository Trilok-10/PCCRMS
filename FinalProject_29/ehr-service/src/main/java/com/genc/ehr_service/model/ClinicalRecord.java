package com.genc.ehr_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long doctorId;

    private Long appointmentId;

    @Column(nullable = false)
    private LocalDate encounterDate;

    @Column(length = 50)
    private String encounterType; // e.g., "ROUTINE", "FOLLOW_UP", "EMERGENCY"

    @Column(length = 500)
    private String chiefComplaint;

    @Column(columnDefinition = "TEXT")
    private String clinicalNotes;

    @Column(length = 255)
    private String vitalsSummary;

    @Column(length = 255)
    private String diagnosisCode;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

