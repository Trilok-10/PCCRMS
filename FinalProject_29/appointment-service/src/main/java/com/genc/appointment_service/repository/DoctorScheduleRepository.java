package com.genc.appointment_service.repository;

import com.genc.appointment_service.model.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    // Find all schedules for a doctor
    List<DoctorSchedule> findByDoctorId(Long doctorId);

    // Find schedule for doctor on specific day
    Optional<DoctorSchedule> findByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);

    // Find available schedules for a doctor
    List<DoctorSchedule> findByDoctorIdAndIsAvailableTrue(Long doctorId);

    // Check if schedule exists
    boolean existsByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);

    // Delete all schedules for a doctor (cascade)
    void deleteByDoctorId(Long doctorId);
}

