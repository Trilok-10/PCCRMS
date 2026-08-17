package com.genc.ehr_service.repository;

import com.genc.ehr_service.model.ClinicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, Long> {

    // Find all records for a patient (history)
    List<ClinicalRecord> findByPatientIdOrderByEncounterDateDesc(Long patientId);

    // Find records by doctor
    List<ClinicalRecord> findByDoctorIdOrderByEncounterDateDesc(Long doctorId);

    // Find records by appointment
    List<ClinicalRecord> findByAppointmentId(Long appointmentId);

    // Find records by patient and date range
    List<ClinicalRecord> findByPatientIdAndEncounterDateBetween(Long patientId, LocalDate startDate, LocalDate endDate);

    // Find records by encounter date
    List<ClinicalRecord> findByEncounterDate(LocalDate date);

    // Count records by patient
    Long countByPatientId(Long patientId);

    // Find latest record for patient
    @Query("SELECT cr FROM ClinicalRecord cr WHERE cr.patientId = :patientId ORDER BY cr.encounterDate DESC, cr.createdAt DESC LIMIT 1")
    ClinicalRecord findLatestByPatientId(@Param("patientId") Long patientId);

    // Delete all records for a patient (cascade)
    void deleteByPatientId(Long patientId);

    // Delete all records for a doctor (cascade)
    void deleteByDoctorId(Long doctorId);
}

