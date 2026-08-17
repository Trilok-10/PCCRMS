package com.genc.ehr_service.service;

import com.genc.ehr_service.dto.*;
import com.genc.ehr_service.model.ClinicalRecord;
import com.genc.ehr_service.repository.ClinicalRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EhrService {

    private final ClinicalRecordRepository clinicalRecordRepository;

    // Create new encounter
    @Transactional
    public ClinicalRecordDTO createEncounter(EncounterRequest request) {
        log.info("Creating encounter for patient {}", request.getPatientId());

        ClinicalRecord record = ClinicalRecord.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .appointmentId(request.getAppointmentId())
                .encounterDate(request.getEncounterDate())
                .encounterType(request.getEncounterType())
                .chiefComplaint(request.getChiefComplaint())
                .clinicalNotes(request.getClinicalNotes())
                .diagnosisCode(request.getDiagnosisCode())
                .vitalsSummary(request.getVitalsSummary())
                .build();

        ClinicalRecord saved = clinicalRecordRepository.save(record);
        log.info("Encounter created with ID: {}", saved.getRecordId());

        return mapToDTO(saved);
    }

    // Update encounter notes
    @Transactional
    public ClinicalRecordDTO updateEncounterNotes(Long recordId, String clinicalNotes) {
        ClinicalRecord record = clinicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Clinical record not found"));

        record.setClinicalNotes(clinicalNotes);
        return mapToDTO(clinicalRecordRepository.save(record));
    }



    // Get patient history
    public List<ClinicalRecordDTO> getPatientHistory(Long patientId) {
        log.info("Fetching patient history for patient {}", patientId);
        return clinicalRecordRepository.findByPatientIdOrderByEncounterDateDesc(patientId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get record by ID
    public ClinicalRecordDTO getRecordById(Long recordId) {
        ClinicalRecord record = clinicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Clinical record not found"));
        return mapToDTO(record);
    }

    // Get record by appointment
    public List<ClinicalRecordDTO> getRecordsByAppointment(Long appointmentId) {
        return clinicalRecordRepository.findByAppointmentId(appointmentId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    // Get all records
    public List<ClinicalRecordDTO> getAllRecords() {
        return clinicalRecordRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    // Map ClinicalRecord to DTO
    private ClinicalRecordDTO mapToDTO(ClinicalRecord record) {
        return ClinicalRecordDTO.builder()
                .recordId(record.getRecordId())
                .patientId(record.getPatientId())
                .doctorId(record.getDoctorId())
                .appointmentId(record.getAppointmentId())
                .encounterDate(record.getEncounterDate())
                .encounterType(record.getEncounterType())
                .chiefComplaint(record.getChiefComplaint())
                .clinicalNotes(record.getClinicalNotes())
                .vitalsSummary(record.getVitalsSummary())
                .diagnosisCode(record.getDiagnosisCode())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }



    // Delete all records for a patient (cascade deletion)
    @Transactional
    public void deleteByPatientId(Long patientId) {
        log.info("Cascade deleting all clinical records for patient ID: {}", patientId);
        clinicalRecordRepository.deleteByPatientId(patientId);
    }

    // Delete all records for a doctor (cascade deletion)
    @Transactional
    public void deleteByDoctorId(Long doctorId) {
        log.info("Cascade deleting all clinical records for doctor ID: {}", doctorId);
        clinicalRecordRepository.deleteByDoctorId(doctorId);
    }
}

