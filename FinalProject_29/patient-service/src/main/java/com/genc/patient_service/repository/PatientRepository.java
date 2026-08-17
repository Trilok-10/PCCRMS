package com.genc.patient_service.repository;

import com.genc.patient_service.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Find by MRN
    Optional<Patient> findByMrn(String mrn);

    // Find by user ID (linked to auth service)
    Optional<Patient> findByUserId(Long userId);

    // Check if MRN exists
    boolean existsByMrn(String mrn);

    // Check if email exists
    boolean existsByEmail(String email);

    // Find by email
    Optional<Patient> findByEmail(String email);

    // Search by name (case insensitive)
    List<Patient> findByFullNameContainingIgnoreCase(String name);

    // Search by contact number
    List<Patient> findByContactNumberContaining(String phone);

    // Find all active patients
    List<Patient> findByActiveTrue();

    // Search patients by name, MRN, or phone
    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "p.mrn LIKE CONCAT('%', :keyword, '%') OR " +
           "p.contactNumber LIKE CONCAT('%', :keyword, '%')")
    List<Patient> searchPatients(@Param("keyword") String keyword);
}

