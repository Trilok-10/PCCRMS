package com.genc.ehr_service.controller;

import com.genc.ehr_service.dto.*;
import com.genc.ehr_service.service.EhrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ehr")
@RequiredArgsConstructor
@Slf4j
public class EhrController {

    private final EhrService ehrService;

    // Create new encounter
    @PostMapping("/encounters")
    public ResponseEntity<ApiResponse<ClinicalRecordDTO>> createEncounter(
            @Valid @RequestBody EncounterRequest request) {
        ClinicalRecordDTO record = ehrService.createEncounter(request);
        return ResponseEntity.ok(ApiResponse.success("Encounter created successfully", record));
    }

    // Update encounter notes
    @PutMapping("/encounters/{recordId}/notes")
    public ResponseEntity<ApiResponse<ClinicalRecordDTO>> updateEncounterNotes(
            @PathVariable Long recordId,
            @RequestBody String clinicalNotes) {
        ClinicalRecordDTO record = ehrService.updateEncounterNotes(recordId, clinicalNotes);
        return ResponseEntity.ok(ApiResponse.success("Notes updated successfully", record));
    }



    // Get patient history
    @GetMapping("/patients/{patientId}/history")
    public ResponseEntity<ApiResponse<List<ClinicalRecordDTO>>> getPatientHistory(
            @PathVariable Long patientId) {
        List<ClinicalRecordDTO> records = ehrService.getPatientHistory(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient history retrieved", records));
    }

    // Get record by ID
    @GetMapping("/records/{recordId}")
    public ResponseEntity<ApiResponse<ClinicalRecordDTO>> getRecordById(@PathVariable Long recordId) {
        ClinicalRecordDTO record = ehrService.getRecordById(recordId);
        return ResponseEntity.ok(ApiResponse.success(record));
    }

    // Get records by appointment
    @GetMapping("/appointments/{appointmentId}/records")
    public ResponseEntity<ApiResponse<List<ClinicalRecordDTO>>> getRecordsByAppointment(
            @PathVariable Long appointmentId) {
        List<ClinicalRecordDTO> records = ehrService.getRecordsByAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.success(records));
    }



    // Get all records
    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<ClinicalRecordDTO>>> getAllRecords() {
        List<ClinicalRecordDTO> records = ehrService.getAllRecords();
        return ResponseEntity.ok(ApiResponse.success(records));
    }



    // Cascade delete: Delete all records for a patient
    @DeleteMapping("/patients/{patientId}/records")
    public ResponseEntity<ApiResponse<String>> deleteByPatientId(@PathVariable Long patientId) {
        ehrService.deleteByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient clinical records deleted", null));
    }

    // Cascade delete: Delete all records by a doctor
    @DeleteMapping("/doctors/{doctorId}/records")
    public ResponseEntity<ApiResponse<String>> deleteByDoctorId(@PathVariable Long doctorId) {
        ehrService.deleteByDoctorId(doctorId);
        return ResponseEntity.ok(ApiResponse.success("Doctor clinical records deleted", null));
    }
}
