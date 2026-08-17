package com.genc.pharmacy_service.repository;

import com.genc.pharmacy_service.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    // Find by name
    Optional<Medication> findByNameIgnoreCase(String name);

    // Search by name
    List<Medication> findByNameContainingIgnoreCase(String name);

    // Find by category
    List<Medication> findByCategory(String category);

    // Find active medications
    List<Medication> findByActiveTrue();

    // Find low stock medications
    @Query("SELECT m FROM Medication m WHERE m.stockQuantity <= m.reorderLevel AND m.active = true")
    List<Medication> findLowStockMedications();

    // Find expiring soon
    @Query("SELECT m FROM Medication m WHERE m.expiryDate <= :date AND m.active = true")
    List<Medication> findExpiringSoon(@Param("date") LocalDate date);

    // Check if exists by name
    boolean existsByNameIgnoreCase(String name);
}

