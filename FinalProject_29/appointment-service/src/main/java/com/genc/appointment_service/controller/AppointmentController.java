package com.genc.appointment_service.controller;

import com.genc.appointment_service.dto.*;
import com.genc.appointment_service.model.AppointmentStatus;
import com.genc.appointment_service.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;

    // Book new appointment
    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentDTO>> bookAppointment(
            @Valid @RequestBody AppointmentRequest request) {
        AppointmentDTO appointment = appointmentService.bookAppointment(request);
        return ResponseEntity.ok(ApiResponse.success("Appointment booked successfully", appointment));
    }

    // Reschedule appointment
    @PutMapping("/{appointmentId}/reschedule")
    public ResponseEntity<ApiResponse<AppointmentDTO>> rescheduleAppointment(
            @PathVariable Long appointmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime newTime) {
        AppointmentDTO appointment = appointmentService.rescheduleAppointment(appointmentId, newDate, newTime);
        return ResponseEntity.ok(ApiResponse.success("Appointment rescheduled successfully", appointment));
    }

    // Cancel appointment
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<ApiResponse<AppointmentDTO>> cancelAppointment(@PathVariable Long appointmentId) {
        AppointmentDTO appointment = appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.success("Appointment cancelled successfully", appointment));
    }

    // Update appointment status
    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<ApiResponse<AppointmentDTO>> updateStatus(
            @PathVariable Long appointmentId,
            @RequestParam AppointmentStatus status) {
        AppointmentDTO appointment = appointmentService.updateStatus(appointmentId, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", appointment));
    }

    // Get doctor's available slots
    @GetMapping("/slots")
    public ResponseEntity<ApiResponse<List<TimeSlotDTO>>> getDoctorSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TimeSlotDTO> slots = appointmentService.getDoctorSlots(doctorId, date);
        return ResponseEntity.ok(ApiResponse.success("Available slots retrieved", slots));
    }

    // Get appointment by ID
    @GetMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<AppointmentDTO>> getAppointmentById(@PathVariable Long appointmentId) {
        AppointmentDTO appointment = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(ApiResponse.success(appointment));
    }

    // Get patient's appointments
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getPatientAppointments(@PathVariable Long patientId) {
        List<AppointmentDTO> appointments = appointmentService.getPatientAppointments(patientId);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }

    // Get doctor's appointments
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getDoctorAppointments(@PathVariable Long doctorId) {
        List<AppointmentDTO> appointments = appointmentService.getDoctorAppointments(doctorId);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }

    // Get doctor's today appointments
    @GetMapping("/doctor/{doctorId}/today")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getDoctorTodayAppointments(@PathVariable Long doctorId) {
        List<AppointmentDTO> appointments = appointmentService.getDoctorTodayAppointments(doctorId);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }

    // Get all appointments
    @GetMapping
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAllAppointments() {
        List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }

    // Get appointments by date
    @GetMapping("/date")
    public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAppointmentsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(ApiResponse.success(appointments));
    }

    // Cascade delete: Delete all appointments for a patient
    @DeleteMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<String>> deleteByPatientId(@PathVariable Long patientId) {
        appointmentService.deleteByPatientId(patientId);
        return ResponseEntity.ok(ApiResponse.success("Patient appointments deleted", null));
    }

    // Cascade delete: Delete all appointments and schedules for a doctor
    @DeleteMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<String>> deleteByDoctorId(@PathVariable Long doctorId) {
        appointmentService.deleteByDoctorId(doctorId);
        return ResponseEntity.ok(ApiResponse.success("Doctor appointments and schedules deleted", null));
    }
}
