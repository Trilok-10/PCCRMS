package com.genc.pharmacy_service.service;

import com.genc.pharmacy_service.client.EhrServiceClient;
import com.genc.pharmacy_service.dto.*;
import com.genc.pharmacy_service.model.*;
import com.genc.pharmacy_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PharmacyService {

    private final PrescriptionRepository prescriptionRepository;
    private final MedicationRepository medicationRepository;
    private final EhrServiceClient ehrServiceClient;

    // ==================== PRESCRIPTION OPERATIONS ====================

    // Create prescription
    @Transactional
    public PrescriptionDTO createPrescription(PrescriptionRequest request) {
        log.info("Creating prescription for patient {}", request.getPatientId());

        Prescription prescription = Prescription.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .recordId(request.getRecordId())
                .medicationName(request.getMedicationName())
                .dosage(request.getDosage())
                .frequency(request.getFrequency())
                .duration(request.getDuration())
                .quantity(request.getQuantity())
                .instructions(request.getInstructions())
                .notes(request.getNotes())
                .dispenseStatus(DispenseStatus.PENDING)
                .prescriptionDate(LocalDate.now())
                .build();

        Prescription saved = prescriptionRepository.save(prescription);
        log.info("Prescription created with ID: {}", saved.getPrescriptionId());

        return mapToDTO(saved);
    }

    // Dispense medication
    @Transactional
    public PrescriptionDTO dispenseMedication(Long prescriptionId, Long pharmacistId) {
        log.info("Dispensing medication for prescription {}", prescriptionId);

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        if (prescription.getDispenseStatus() == DispenseStatus.DISPENSED) {
            throw new RuntimeException("Medication already dispensed");
        }

        if (prescription.getDispenseStatus() == DispenseStatus.CANCELLED) {
            throw new RuntimeException("Prescription is cancelled");
        }

        // Update stock if medication exists in inventory
        Medication medication = medicationRepository.findByNameIgnoreCase(prescription.getMedicationName())
                .orElse(null);

        if (medication != null && prescription.getQuantity() != null) {
            if (medication.getStockQuantity() < prescription.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + medication.getName());
            }
            medication.setStockQuantity(medication.getStockQuantity() - prescription.getQuantity());
            medicationRepository.save(medication);
        }

        prescription.setDispenseStatus(DispenseStatus.DISPENSED);
        prescription.setDispensedAt(LocalDateTime.now());
        prescription.setDispensedBy(pharmacistId);

        Prescription updated = prescriptionRepository.save(prescription);
        log.info("Medication dispensed successfully");

        // Update EHR record via Feign call to ehr-service (if recordId is linked)
        // Failure is logged but does NOT block the dispense operation
        updateEhrRecord(updated);

        return mapToDTO(updated);
    }

    /**
     * Update the EHR clinical notes to record that medication was dispensed.
     * If ehr-service is unavailable or recordId is null, logs a warning but doesn't fail.
     */
    private void updateEhrRecord(Prescription prescription) {
        if (prescription.getRecordId() == null) {
            log.info("No EHR record linked to prescription {} — skipping EHR update", prescription.getPrescriptionId());
            return;
        }

        try {
            String noteUpdate = String.format(
                    "[PHARMACY] Medication dispensed: %s, Dosage: %s, Quantity: %d, Dispensed at: %s",
                    prescription.getMedicationName(),
                    prescription.getDosage() != null ? prescription.getDosage() : "N/A",
                    prescription.getQuantity() != null ? prescription.getQuantity() : 0,
                    prescription.getDispensedAt()
            );

            ApiResponse<ClinicalRecordDTO> response = ehrServiceClient.updateEncounterNotes(
                    prescription.getRecordId(), noteUpdate);

            if (response != null && response.isSuccess()) {
                log.info("EHR record {} updated with dispense note for prescription {}",
                        prescription.getRecordId(), prescription.getPrescriptionId());
            } else {
                log.warn("EHR update returned unsuccessful response for prescription {}",
                        prescription.getPrescriptionId());
            }
        } catch (Exception e) {
            // Log warning but don't fail the dispense — EHR can be updated manually
            log.warn("Failed to update EHR record for prescription {}: {}. EHR can be updated manually.",
                    prescription.getPrescriptionId(), e.getMessage());
        }
    }

    // Cancel prescription
    @Transactional
    public PrescriptionDTO cancelPrescription(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        if (prescription.getDispenseStatus() == DispenseStatus.DISPENSED) {
            throw new RuntimeException("Cannot cancel a dispensed prescription");
        }

        prescription.setDispenseStatus(DispenseStatus.CANCELLED);
        return mapToDTO(prescriptionRepository.save(prescription));
    }

    // Get prescription by ID
    public PrescriptionDTO getPrescriptionById(Long prescriptionId) {
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
        return mapToDTO(prescription);
    }

    // Get patient prescriptions
    public List<PrescriptionDTO> getPatientPrescriptions(Long patientId) {
        return prescriptionRepository.findByPatientIdOrderByPrescriptionDateDesc(patientId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get pending prescriptions
    public List<PrescriptionDTO> getPendingPrescriptions() {
        return prescriptionRepository.findByDispenseStatusOrderByCreatedAtAsc(DispenseStatus.PENDING)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get all prescriptions
    public List<PrescriptionDTO> getAllPrescriptions() {
        return prescriptionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get prescriptions by status
    public List<PrescriptionDTO> getPrescriptionsByStatus(DispenseStatus status) {
        return prescriptionRepository.findByDispenseStatus(status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ==================== MEDICATION/INVENTORY OPERATIONS ====================

    // Add medication to inventory
    @Transactional
    public MedicationDTO addMedication(MedicationRequest request) {
        if (medicationRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException("Medication already exists");
        }

        Medication medication = Medication.builder()
                .name(request.getName())
                .genericName(request.getGenericName())
                .manufacturer(request.getManufacturer())
                .category(request.getCategory())
                .dosageForm(request.getDosageForm())
                .strength(request.getStrength())
                .stockQuantity(request.getStockQuantity())
                .reorderLevel(request.getReorderLevel())
                .unitPrice(request.getUnitPrice())
                .expiryDate(request.getExpiryDate())
                .contraindications(request.getContraindications())
                .active(true)
                .build();

        return mapMedicationToDTO(medicationRepository.save(medication));
    }

    // Update medication stock
    @Transactional
    public MedicationDTO updateStock(Long medicationId, Integer quantity) {
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new RuntimeException("Medication not found"));

        medication.setStockQuantity(medication.getStockQuantity() + quantity);
        return mapMedicationToDTO(medicationRepository.save(medication));
    }

    // Delete medication
    @Transactional
    public void deleteMedication(Long medicationId) {
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new RuntimeException("Medication not found"));
        // Soft delete by setting active to false, or hard delete. Let's do hard delete since there's no active flag logic specified for delete, but wait, there is an active flag!
        // Wait, line 235 sets active(true), and getAllMedications filters by activeTrue(). So soft delete is appropriate.
        medication.setActive(false);
        medicationRepository.save(medication);
    }

    // Get medication by ID
    public MedicationDTO getMedicationById(Long medicationId) {
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new RuntimeException("Medication not found"));
        return mapMedicationToDTO(medication);
    }

    // Search medications
    public List<MedicationDTO> searchMedications(String name) {
        return medicationRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapMedicationToDTO)
                .collect(Collectors.toList());
    }

    // Get all medications
    public List<MedicationDTO> getAllMedications() {
        return medicationRepository.findByActiveTrue()
                .stream()
                .map(this::mapMedicationToDTO)
                .collect(Collectors.toList());
    }

    // Get low stock medications
    public List<MedicationDTO> getLowStockMedications() {
        return medicationRepository.findLowStockMedications()
                .stream()
                .map(this::mapMedicationToDTO)
                .collect(Collectors.toList());
    }

    // Get expiring medications
    public List<MedicationDTO> getExpiringMedications(int days) {
        LocalDate expiryDate = LocalDate.now().plusDays(days);
        return medicationRepository.findExpiringSoon(expiryDate)
                .stream()
                .map(this::mapMedicationToDTO)
                .collect(Collectors.toList());
    }

    // ==================== MAPPING METHODS ====================

    private PrescriptionDTO mapToDTO(Prescription prescription) {
        return PrescriptionDTO.builder()
                .prescriptionId(prescription.getPrescriptionId())
                .patientId(prescription.getPatientId())
                .doctorId(prescription.getDoctorId())
                .recordId(prescription.getRecordId())
                .medicationName(prescription.getMedicationName())
                .dosage(prescription.getDosage())
                .frequency(prescription.getFrequency())
                .duration(prescription.getDuration())
                .quantity(prescription.getQuantity())
                .instructions(prescription.getInstructions())
                .dispenseStatus(prescription.getDispenseStatus())
                .prescriptionDate(prescription.getPrescriptionDate())
                .dispensedAt(prescription.getDispensedAt())
                .dispensedBy(prescription.getDispensedBy())
                .notes(prescription.getNotes())
                .createdAt(prescription.getCreatedAt())
                .updatedAt(prescription.getUpdatedAt())
                .build();
    }

    private MedicationDTO mapMedicationToDTO(Medication medication) {
        return MedicationDTO.builder()
                .medicationId(medication.getMedicationId())
                .name(medication.getName())
                .genericName(medication.getGenericName())
                .manufacturer(medication.getManufacturer())
                .category(medication.getCategory())
                .dosageForm(medication.getDosageForm())
                .strength(medication.getStrength())
                .stockQuantity(medication.getStockQuantity())
                .reorderLevel(medication.getReorderLevel())
                .unitPrice(medication.getUnitPrice())
                .expiryDate(medication.getExpiryDate())
                .contraindications(medication.getContraindications())
                .active(medication.getActive())
                .createdAt(medication.getCreatedAt())
                .updatedAt(medication.getUpdatedAt())
                .build();
    }


    // Delete all prescriptions for a patient (cascade deletion)
    @Transactional
    public void deleteByPatientId(Long patientId) {
        log.info("Cascade deleting all prescriptions for patient ID: {}", patientId);
        prescriptionRepository.deleteByPatientId(patientId);
    }

    // Delete all prescriptions for a doctor (cascade deletion)
    @Transactional
    public void deleteByDoctorId(Long doctorId) {
        log.info("Cascade deleting all prescriptions for doctor ID: {}", doctorId);
        prescriptionRepository.deleteByDoctorId(doctorId);
    }
}

