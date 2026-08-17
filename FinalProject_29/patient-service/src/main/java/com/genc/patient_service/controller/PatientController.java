package com.genc.patient_service.controller;

import com.genc.patient_service.dto.ApiResponse;
import com.genc.patient_service.dto.PatientDTO;
import com.genc.patient_service.dto.PatientRequest;
import com.genc.patient_service.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    // Register new patient
    @PostMapping
    public ResponseEntity<ApiResponse<PatientDTO>> registerPatient(
            @Valid @RequestBody PatientRequest request) {
        PatientDTO patient = patientService.registerPatient(request);
        return ResponseEntity.ok(ApiResponse.success("Patient registered successfully", patient));
    }

    // Update patient demographics
    @PutMapping("/{patientId}")
    public ResponseEntity<ApiResponse<PatientDTO>> updateDemographics(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientRequest request) {
        PatientDTO patient = patientService.updateDemographics(patientId, request);
        return ResponseEntity.ok(ApiResponse.success("Patient demographics updated", patient));
    }

    // Get patient by ID
    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientById(@PathVariable Long patientId) {
        PatientDTO patient = patientService.getPatientById(patientId);
        return ResponseEntity.ok(ApiResponse.success(patient));
    }

    // Get patient by MRN
    @GetMapping("/mrn/{mrn}")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientByMrn(@PathVariable String mrn) {
        PatientDTO patient = patientService.getPatientByMrn(mrn);
        return ResponseEntity.ok(ApiResponse.success(patient));
    }

    // Get patient by user ID (for patient portal)
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientByUserId(@PathVariable Long userId) {
        PatientDTO patient = patientService.getPatientByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(patient));
    }

    // Search patients (by name, MRN, or phone)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> searchPatients(
            @RequestParam String keyword) {
        List<PatientDTO> patients = patientService.searchPatients(keyword);
        return ResponseEntity.ok(ApiResponse.success("Found " + patients.size() + " patients", patients));
    }

    // Get all patients
    @GetMapping
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getAllPatients() {
        List<PatientDTO> patients = patientService.getAllPatients();
        return ResponseEntity.ok(ApiResponse.success(patients));
    }

    // Get all active patients
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getActivePatients() {
        List<PatientDTO> patients = patientService.getActivePatients();
        return ResponseEntity.ok(ApiResponse.success(patients));
    }

    // Deactivate patient
    @PutMapping("/{patientId}/deactivate")
    public ResponseEntity<ApiResponse<PatientDTO>> deactivatePatient(@PathVariable Long patientId) {
        PatientDTO patient = patientService.deactivatePatient(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient deactivated", patient));
    }

    // Activate patient
    @PutMapping("/{patientId}/activate")
    public ResponseEntity<ApiResponse<PatientDTO>> activatePatient(@PathVariable Long patientId) {
        PatientDTO patient = patientService.activatePatient(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient activated", patient));
    }

    // Delete patient permanently
    @DeleteMapping("/{patientId}")
    public ResponseEntity<ApiResponse<String>> deletePatient(@PathVariable Long patientId) {
        patientService.deletePatient(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient deleted successfully", null));
    }

    // Delete patient by user ID (cascade from auth-service)
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<String>> deletePatientByUserId(@PathVariable Long userId) {
        patientService.deletePatientByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Patient deleted successfully by user ID", null));
    }
}

