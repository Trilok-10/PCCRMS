package com.genc.appointment_service.repository;

import com.genc.appointment_service.model.Appointment;
import com.genc.appointment_service.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Find appointments by patient
    List<Appointment> findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(Long patientId);

    // Find appointments by doctor
    List<Appointment> findByDoctorIdOrderByAppointmentDateAscAppointmentTimeAsc(Long doctorId);

    // Find appointments by doctor and date
    List<Appointment> findByDoctorIdAndAppointmentDate(Long doctorId, LocalDate date);

    // Find appointments by patient and status
    List<Appointment> findByPatientIdAndAppointmentStatus(Long patientId, AppointmentStatus status);

    // Find appointments by doctor and status
    List<Appointment> findByDoctorIdAndAppointmentStatus(Long doctorId, AppointmentStatus status);

    // Find today's appointments for a doctor
    @Query("SELECT a FROM Appointment a WHERE a.doctorId = :doctorId AND a.appointmentDate = :date ORDER BY a.appointmentTime")
    List<Appointment> findTodaysAppointments(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    // Check if slot is already booked
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctorId = :doctorId AND a.appointmentDate = :date AND a.appointmentTime = :time AND a.appointmentStatus != 'CANCELLED'")
    boolean isSlotBooked(@Param("doctorId") Long doctorId, @Param("date") LocalDate date, @Param("time") LocalTime time);

    // Find appointments by date range
    List<Appointment> findByAppointmentDateBetween(LocalDate startDate, LocalDate endDate);

    // Find appointments by doctor and date range
    List<Appointment> findByDoctorIdAndAppointmentDateBetween(Long doctorId, LocalDate startDate, LocalDate endDate);

    // Find all appointments for a specific date
    List<Appointment> findByAppointmentDate(LocalDate date);

    // Count appointments by status for a doctor
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctorId = :doctorId AND a.appointmentStatus = :status")
    Long countByDoctorIdAndStatus(@Param("doctorId") Long doctorId, @Param("status") AppointmentStatus status);

    // Delete all appointments for a patient (cascade)
    void deleteByPatientId(Long patientId);

    // Delete all appointments for a doctor (cascade)
    void deleteByDoctorId(Long doctorId);
}

