package com.genc.pharmacy_service.repository;

import com.genc.pharmacy_service.model.DispenseStatus;
import com.genc.pharmacy_service.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    // Find prescriptions by patient
    List<Prescription> findByPatientIdOrderByPrescriptionDateDesc(Long patientId);

    // Find prescriptions by doctor
    List<Prescription> findByDoctorIdOrderByPrescriptionDateDesc(Long doctorId);

    // Find prescriptions by status
    List<Prescription> findByDispenseStatus(DispenseStatus status);

    // Find pending prescriptions
    List<Prescription> findByDispenseStatusOrderByCreatedAtAsc(DispenseStatus status);

    // Find prescriptions by patient and status
    List<Prescription> findByPatientIdAndDispenseStatus(Long patientId, DispenseStatus status);

    // Find prescriptions by date
    List<Prescription> findByPrescriptionDate(LocalDate date);

    // Find prescriptions by clinical record
    List<Prescription> findByRecordId(Long recordId);

    // Count pending prescriptions
    Long countByDispenseStatus(DispenseStatus status);

    // Delete all prescriptions for a patient (cascade)
    void deleteByPatientId(Long patientId);

    // Delete all prescriptions for a doctor (cascade)
    void deleteByDoctorId(Long doctorId);
}

