package com.genc.pharmacy_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicationId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 100)
    private String genericName;

    @Column(length = 100)
    private String manufacturer;

    @Column(length = 50)
    private String category; // e.g., "Antibiotic", "Painkiller"

    @Column(length = 50)
    private String dosageForm; // e.g., "Tablet", "Capsule", "Syrup"

    @Column(length = 50)
    private String strength; // e.g., "500mg", "250mg/5ml"

    private Integer stockQuantity;

    private Integer reorderLevel;

    private BigDecimal unitPrice;

    private LocalDate expiryDate;

    @Column(length = 500)
    private String contraindications; // Known drug interactions/warnings

    @Column(nullable = false)
    private Boolean active = true;

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

