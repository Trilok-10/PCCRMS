package com.genc.pharmacy_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prescriptionId;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long doctorId;

    private Long recordId; // Clinical record reference

    @Column(nullable = false, length = 100)
    private String medicationName;

    @Column(length = 50)
    private String dosage; // e.g., "500mg"

    @Column(length = 50)
    private String frequency; // e.g., "Twice daily"

    @Column(length = 50)
    private String duration; // e.g., "7 days"

    private Integer quantity;

    @Column(length = 500)
    private String instructions; // e.g., "Take after meals"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispenseStatus dispenseStatus;

    private LocalDate prescriptionDate;

    private LocalDateTime dispensedAt;

    private Long dispensedBy; // Pharmacist user ID

    @Column(length = 255)
    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (dispenseStatus == null) {
            dispenseStatus = DispenseStatus.PENDING;
        }
        if (prescriptionDate == null) {
            prescriptionDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

