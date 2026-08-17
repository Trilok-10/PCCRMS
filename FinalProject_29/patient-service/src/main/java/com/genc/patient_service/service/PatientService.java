package com.genc.patient_service.service;

import com.genc.patient_service.dto.PatientDTO;
import com.genc.patient_service.dto.PatientRequest;
import com.genc.patient_service.model.Patient;
import com.genc.patient_service.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final com.genc.patient_service.client.AppointmentServiceClient appointmentServiceClient;
    private final com.genc.patient_service.client.BillingServiceClient billingServiceClient;
    private final com.genc.patient_service.client.EhrServiceClient ehrServiceClient;
    private final com.genc.patient_service.client.PharmacyServiceClient pharmacyServiceClient;
    private final com.genc.patient_service.client.AuthServiceClient authServiceClient;

    // Register new patient
    @Transactional
    public PatientDTO registerPatient(PatientRequest request) {
        log.info("Registering new patient: {}", request.getFullName());

        // Check for duplicate email if provided
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (patientRepository.existsByEmail(request.getEmail().toLowerCase())) {
                throw new RuntimeException("Patient with this email already exists");
            }
        }

        // Generate unique MRN
        String mrn = generateMRN();

        Patient patient = Patient.builder()
                .mrn(mrn)
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .contactNumber(request.getContactNumber())
                .email(request.getEmail() != null ? request.getEmail().toLowerCase() : null)
                .address(request.getAddress())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyPhone(request.getEmergencyPhone())
                .bloodGroup(request.getBloodGroup())
                .allergies(request.getAllergies())
                .userId(request.getUserId())
                .active(true)
                .build();

        Patient saved = patientRepository.save(patient);
        log.info("Patient registered successfully with MRN: {}", mrn);

        return mapToDTO(saved);
    }

    // Generate unique MRN (Medical Record Number)
    private String generateMRN() {
        String prefix = "MRN";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String random = String.format("%04d", new Random().nextInt(10000));
        String mrn = prefix + timestamp + random;

        // Ensure uniqueness
        while (patientRepository.existsByMrn(mrn)) {
            random = String.format("%04d", new Random().nextInt(10000));
            mrn = prefix + timestamp + random;
        }

        return mrn;
    }

    // Update patient demographics
    @Transactional
    public PatientDTO updateDemographics(Long patientId, PatientRequest request) {
        log.info("Updating demographics for patient ID: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));

        // Check email uniqueness if changed
        if (request.getEmail() != null && !request.getEmail().toLowerCase().equals(patient.getEmail())) {
            if (patientRepository.existsByEmail(request.getEmail().toLowerCase())) {
                throw new RuntimeException("Email already in use by another patient");
            }
        }

        patient.setFullName(request.getFullName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setContactNumber(request.getContactNumber());
        patient.setEmail(request.getEmail() != null ? request.getEmail().toLowerCase() : null);
        patient.setAddress(request.getAddress());
        patient.setEmergencyContactName(request.getEmergencyContactName());
        patient.setEmergencyPhone(request.getEmergencyPhone());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setAllergies(request.getAllergies());

        Patient updated = patientRepository.save(patient);
        log.info("Patient demographics updated successfully");

        return mapToDTO(updated);
    }

    // Get patient by ID
    public PatientDTO getPatientById(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));
        return mapToDTO(patient);
    }

    // Get patient by MRN
    public PatientDTO getPatientByMrn(String mrn) {
        Patient patient = patientRepository.findByMrn(mrn)
                .orElseThrow(() -> new RuntimeException("Patient not found with MRN: " + mrn));
        return mapToDTO(patient);
    }

    // Get patient by user ID (for patient login)
    public PatientDTO getPatientByUserId(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient not found with User ID: " + userId));
        return mapToDTO(patient);
    }

    // Search patients
    public List<PatientDTO> searchPatients(String keyword) {
        log.info("Searching patients with keyword: {}", keyword);
        List<Patient> patients = patientRepository.searchPatients(keyword);
        return patients.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Get all patients
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get all active patients
    public List<PatientDTO> getActivePatients() {
        return patientRepository.findByActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Deactivate patient
    @Transactional
    public PatientDTO deactivatePatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));
        patient.setActive(false);
        return mapToDTO(patientRepository.save(patient));
    }

    // Activate patient
    @Transactional
    public PatientDTO activatePatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));
        patient.setActive(true);
        return mapToDTO(patientRepository.save(patient));
    }

    // Delete patient by User ID
    @Transactional
    public void deletePatientByUserId(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient not found with User ID: " + userId));
        log.info("Deleting patient with User ID: {}", userId);
        deletePatient(patient.getPatientId());
    }

    // Delete patient permanently
    @Transactional
    public void deletePatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));
        log.info("Deleting patient with ID: {} and MRN: {}", patientId, patient.getMrn());

        // Cascading deletion using Feign Clients
        try {
            appointmentServiceClient.deleteByPatientId(patientId);
        } catch (Exception e) {
            log.warn("Failed to delete patient appointments from appointment-service: {}", e.getMessage());
        }

        try {
            billingServiceClient.deleteByPatientId(patientId);
        } catch (Exception e) {
            log.warn("Failed to delete patient invoices from billing-service: {}", e.getMessage());
        }

        try {
            ehrServiceClient.deleteByPatientId(patientId);
        } catch (Exception e) {
            log.warn("Failed to delete patient clinical records from ehr-service: {}", e.getMessage());
        }

        try {
            pharmacyServiceClient.deleteByPatientId(patientId);
        } catch (Exception e) {
            log.warn("Failed to delete patient prescriptions from pharmacy-service: {}", e.getMessage());
        }

        Long userId = patient.getUserId();
        
        // Delete the patient record
        patientRepository.delete(patient);
        
        // Delete the associated user record in Auth Service
        if (userId != null) {
            try {
                authServiceClient.deleteUser(userId);
            } catch (Exception e) {
                log.warn("Failed to delete associated user from auth-service: {}", e.getMessage());
            }
        }
    }

    // Map entity to DTO
    private PatientDTO mapToDTO(Patient patient) {
        return PatientDTO.builder()
                .patientId(patient.getPatientId())
                .mrn(patient.getMrn())
                .fullName(patient.getFullName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .contactNumber(patient.getContactNumber())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyPhone(patient.getEmergencyPhone())
                .bloodGroup(patient.getBloodGroup())
                .allergies(patient.getAllergies())
                .userId(patient.getUserId())
                .active(patient.getActive())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}

