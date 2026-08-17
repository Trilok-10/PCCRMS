package com.genc.pharmacy_service.controller;

import com.genc.pharmacy_service.dto.*;
import com.genc.pharmacy_service.model.DispenseStatus;
import com.genc.pharmacy_service.service.PharmacyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
@Slf4j
public class PharmacyController {

    private final PharmacyService pharmacyService;

    // ==================== PRESCRIPTION ENDPOINTS ====================

    // Create prescription
    @PostMapping("/prescriptions")
    public ResponseEntity<ApiResponse<PrescriptionDTO>> createPrescription(
            @Valid @RequestBody PrescriptionRequest request) {
        PrescriptionDTO prescription = pharmacyService.createPrescription(request);
        return ResponseEntity.ok(ApiResponse.success("Prescription created successfully", prescription));
    }

    // Dispense medication
    @PutMapping("/prescriptions/{prescriptionId}/dispense")
    public ResponseEntity<ApiResponse<PrescriptionDTO>> dispenseMedication(
            @PathVariable Long prescriptionId,
            @RequestParam Long pharmacistId) {
        PrescriptionDTO prescription = pharmacyService.dispenseMedication(prescriptionId, pharmacistId);
        return ResponseEntity.ok(ApiResponse.success("Medication dispensed successfully", prescription));
    }

    // Cancel prescription
    @PutMapping("/prescriptions/{prescriptionId}/cancel")
    public ResponseEntity<ApiResponse<PrescriptionDTO>> cancelPrescription(
            @PathVariable Long prescriptionId) {
        PrescriptionDTO prescription = pharmacyService.cancelPrescription(prescriptionId);
        return ResponseEntity.ok(ApiResponse.success("Prescription cancelled", prescription));
    }

    // Get prescription by ID
    @GetMapping("/prescriptions/{prescriptionId}")
    public ResponseEntity<ApiResponse<PrescriptionDTO>> getPrescriptionById(
            @PathVariable Long prescriptionId) {
        PrescriptionDTO prescription = pharmacyService.getPrescriptionById(prescriptionId);
        return ResponseEntity.ok(ApiResponse.success(prescription));
    }

    // Get patient prescriptions
    @GetMapping("/prescriptions/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<PrescriptionDTO>>> getPatientPrescriptions(
            @PathVariable Long patientId) {
        List<PrescriptionDTO> prescriptions = pharmacyService.getPatientPrescriptions(patientId);
        return ResponseEntity.ok(ApiResponse.success(prescriptions));
    }

    // Get pending prescriptions
    @GetMapping("/prescriptions/pending")
    public ResponseEntity<ApiResponse<List<PrescriptionDTO>>> getPendingPrescriptions() {
        List<PrescriptionDTO> prescriptions = pharmacyService.getPendingPrescriptions();
        return ResponseEntity.ok(ApiResponse.success(prescriptions));
    }

    // Get all prescriptions
    @GetMapping("/prescriptions")
    public ResponseEntity<ApiResponse<List<PrescriptionDTO>>> getAllPrescriptions() {
        List<PrescriptionDTO> prescriptions = pharmacyService.getAllPrescriptions();
        return ResponseEntity.ok(ApiResponse.success(prescriptions));
    }

    // Get prescriptions by status
    @GetMapping("/prescriptions/status/{status}")
    public ResponseEntity<ApiResponse<List<PrescriptionDTO>>> getPrescriptionsByStatus(
            @PathVariable DispenseStatus status) {
        List<PrescriptionDTO> prescriptions = pharmacyService.getPrescriptionsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(prescriptions));
    }

    // ==================== MEDICATION/INVENTORY ENDPOINTS ====================

    // Add medication to inventory
    @PostMapping("/medications")
    public ResponseEntity<ApiResponse<MedicationDTO>> addMedication(
            @Valid @RequestBody MedicationRequest request) {
        MedicationDTO medication = pharmacyService.addMedication(request);
        return ResponseEntity.ok(ApiResponse.success("Medication added successfully", medication));
    }

    // Update medication stock
    @PutMapping("/medications/{medicationId}/stock")
    public ResponseEntity<ApiResponse<MedicationDTO>> updateStock(
            @PathVariable Long medicationId,
            @RequestParam Integer quantity) {
        MedicationDTO medication = pharmacyService.updateStock(medicationId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", medication));
    }

    // Delete medication
    @DeleteMapping("/medications/{medicationId}")
    public ResponseEntity<ApiResponse<String>> deleteMedication(@PathVariable Long medicationId) {
        pharmacyService.deleteMedication(medicationId);
        return ResponseEntity.ok(ApiResponse.success("Medication deleted successfully", null));
    }

    // Get medication by ID
    @GetMapping("/medications/{medicationId}")
    public ResponseEntity<ApiResponse<MedicationDTO>> getMedicationById(
            @PathVariable Long medicationId) {
        MedicationDTO medication = pharmacyService.getMedicationById(medicationId);
        return ResponseEntity.ok(ApiResponse.success(medication));
    }

    // Search medications
    @GetMapping("/medications/search")
    public ResponseEntity<ApiResponse<List<MedicationDTO>>> searchMedications(
            @RequestParam String name) {
        List<MedicationDTO> medications = pharmacyService.searchMedications(name);
        return ResponseEntity.ok(ApiResponse.success(medications));
    }

    // Get all medications
    @GetMapping("/medications")
    public ResponseEntity<ApiResponse<List<MedicationDTO>>> getAllMedications() {
        List<MedicationDTO> medications = pharmacyService.getAllMedications();
        return ResponseEntity.ok(ApiResponse.success(medications));
    }

    // Get low stock medications
    @GetMapping("/medications/low-stock")
    public ResponseEntity<ApiResponse<List<MedicationDTO>>> getLowStockMedications() {
        List<MedicationDTO> medications = pharmacyService.getLowStockMedications();
        return ResponseEntity.ok(ApiResponse.success(medications));
    }

    // Get expiring medications
    @GetMapping("/medications/expiring")
    public ResponseEntity<ApiResponse<List<MedicationDTO>>> getExpiringMedications(
            @RequestParam(defaultValue = "30") int days) {
        List<MedicationDTO> medications = pharmacyService.getExpiringMedications(days);
        return ResponseEntity.ok(ApiResponse.success(medications));
    }

    // Cascade delete: Delete all prescriptions for a patient
    @DeleteMapping("/prescriptions/patient/{patientId}")
    public ResponseEntity<ApiResponse<String>> deleteByPatientId(@PathVariable Long patientId) {
        pharmacyService.deleteByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient prescriptions deleted", null));
    }

    // Cascade delete: Delete all prescriptions by a doctor
    @DeleteMapping("/prescriptions/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<String>> deleteByDoctorId(@PathVariable Long doctorId) {
        pharmacyService.deleteByDoctorId(doctorId);
        return ResponseEntity.ok(ApiResponse.success("Doctor prescriptions deleted", null));
    }
}
