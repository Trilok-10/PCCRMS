package com.genc.appointment_service.service;

import com.genc.appointment_service.client.BillingServiceClient;
import com.genc.appointment_service.client.PatientServiceClient;
import com.genc.appointment_service.dto.*;
import com.genc.appointment_service.exception.PatientNotFoundException;
import com.genc.appointment_service.model.Appointment;
import com.genc.appointment_service.model.AppointmentStatus;
import com.genc.appointment_service.model.DoctorSchedule;
import com.genc.appointment_service.repository.AppointmentRepository;
import com.genc.appointment_service.repository.DoctorScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final PatientServiceClient patientServiceClient;
    private final BillingServiceClient billingServiceClient;

    // Book new appointment
    @Transactional
    public AppointmentDTO bookAppointment(AppointmentRequest request) {
        log.info("Booking appointment for patient {} with doctor {}", request.getPatientId(), request.getDoctorId());

        // Step 1: Validate patient exists via Feign call to patient-service
        validatePatientExists(request.getPatientId());

        // Step 2: Check if slot is available
        if (appointmentRepository.isSlotBooked(request.getDoctorId(), request.getAppointmentDate(), request.getAppointmentTime())) {
            throw new RuntimeException("This time slot is already booked");
        }

        // Validate against doctor's schedule
        DayOfWeek dayOfWeek = request.getAppointmentDate().getDayOfWeek();
        DoctorSchedule schedule = doctorScheduleRepository.findByDoctorIdAndDayOfWeek(request.getDoctorId(), dayOfWeek)
                .orElse(null);

        if (schedule != null && !schedule.getIsAvailable()) {
            throw new RuntimeException("Doctor is not available on " + dayOfWeek);
        }

        if (schedule != null) {
            LocalTime requestedTime = request.getAppointmentTime();
            if (requestedTime.isBefore(schedule.getStartTime()) || requestedTime.isAfter(schedule.getEndTime().minusMinutes(schedule.getSlotDurationMinutes()))) {
                throw new RuntimeException("Requested time is outside doctor's working hours");
            }
        }

        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .appointmentStatus(AppointmentStatus.BOOKED)
                .reasonForVisit(request.getReasonForVisit())
                .notes(request.getNotes())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment booked successfully with ID: {}", saved.getAppointmentId());

        // Step 3: Auto-create billing invoice via Feign call to billing-service
        // Failure is logged but does NOT block the appointment creation
        createBillingInvoice(saved);

        return mapToDTO(saved);
    }

    /**
     * Validate that the patient exists and is active via Feign call to patient-service.
     * Throws PatientNotFoundException if patient doesn't exist or is inactive.
     */
    private void validatePatientExists(Long patientId) {
        try {
            ApiResponse<PatientDTO> response = patientServiceClient.getPatientById(patientId);
            if (response == null || !response.isSuccess() || response.getData() == null) {
                throw new PatientNotFoundException(patientId);
            }
            PatientDTO patient = response.getData();
            if (patient.getActive() != null && !patient.getActive()) {
                throw new PatientNotFoundException("Patient with ID " + patientId + " is inactive");
            }
            log.info("Patient validated successfully: {} ({})", patient.getFullName(), patient.getMrn());
        } catch (PatientNotFoundException e) {
            throw e; // Re-throw our custom exception
        } catch (Exception e) {
            log.error("Failed to validate patient via patient-service: {}", e.getMessage());
            throw new PatientNotFoundException("Unable to validate patient. Please try again later.");
        }
    }

    /**
     * Auto-create a billing invoice for the appointment via Feign call to billing-service.
     * If billing-service is unavailable, logs a warning but does NOT fail the appointment.
     */
    private void createBillingInvoice(Appointment appointment) {
        try {
            InvoiceItemRequest consultationItem = InvoiceItemRequest.builder()
                    .serviceName("Consultation Fee")
                    .serviceCode("CONSULT-001")
                    .quantity(1)
                    .unitPrice(new BigDecimal("500.00"))
                    .description("Consultation for appointment #" + appointment.getAppointmentId())
                    .build();

            InvoiceRequest invoiceRequest = InvoiceRequest.builder()
                    .patientId(appointment.getPatientId())
                    .appointmentId(appointment.getAppointmentId())
                    .description("Invoice for appointment on " + appointment.getAppointmentDate())
                    .items(List.of(consultationItem))
                    .build();

            ApiResponse<InvoiceDTO> response = billingServiceClient.generateInvoice(invoiceRequest);
            if (response != null && response.isSuccess() && response.getData() != null) {
                log.info("Billing invoice created successfully: {}", response.getData().getInvoiceNumber());
            } else {
                log.warn("Billing invoice creation returned unsuccessful response for appointment {}", appointment.getAppointmentId());
            }
        } catch (Exception e) {
            // Log warning but don't fail the appointment — billing can be created manually
            log.warn("Failed to create billing invoice for appointment {}: {}. Billing can be created manually.",
                    appointment.getAppointmentId(), e.getMessage());
        }
    }

    // Reschedule appointment
    @Transactional
    public AppointmentDTO rescheduleAppointment(Long appointmentId, LocalDate newDate, LocalTime newTime) {
        log.info("Rescheduling appointment {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getAppointmentStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Cannot reschedule a cancelled appointment");
        }

        if (appointment.getAppointmentStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot reschedule a completed appointment");
        }

        // Check if new slot is available
        if (appointmentRepository.isSlotBooked(appointment.getDoctorId(), newDate, newTime)) {
            throw new RuntimeException("The new time slot is already booked");
        }

        appointment.setAppointmentDate(newDate);
        appointment.setAppointmentTime(newTime);

        Appointment updated = appointmentRepository.save(appointment);
        log.info("Appointment rescheduled successfully");

        return mapToDTO(updated);
    }

    // Cancel appointment
    @Transactional
    public AppointmentDTO cancelAppointment(Long appointmentId) {
        log.info("Cancelling appointment {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getAppointmentStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed appointment");
        }

        appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
        Appointment updated = appointmentRepository.save(appointment);

        log.info("Appointment cancelled successfully");
        return mapToDTO(updated);
    }

    // Update appointment status
    @Transactional
    public AppointmentDTO updateStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setAppointmentStatus(status);
        return mapToDTO(appointmentRepository.save(appointment));
    }

    // Get doctor's available slots for a date
    public List<TimeSlotDTO> getDoctorSlots(Long doctorId, LocalDate date) {
        log.info("Getting available slots for doctor {} on {}", doctorId, date);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        DoctorSchedule schedule = doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek)
                .orElse(null);

        List<TimeSlotDTO> slots = new ArrayList<>();

        if (schedule == null || !schedule.getIsAvailable()) {
            return slots; // No slots available
        }

        // Get already booked slots
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, date)
                .stream()
                .filter(a -> a.getAppointmentStatus() != AppointmentStatus.CANCELLED)
                .collect(Collectors.toList());

        // Check if the requested date is today
        boolean isToday = date.equals(LocalDate.now());
        LocalTime currentTimeNow = LocalTime.now();

        // Generate all possible slots
        LocalTime currentTime = schedule.getStartTime();
        while (currentTime.plusMinutes(schedule.getSlotDurationMinutes()).isBefore(schedule.getEndTime()) 
               || currentTime.plusMinutes(schedule.getSlotDurationMinutes()).equals(schedule.getEndTime())) {
            
            LocalTime slotStart = currentTime;
            LocalTime slotEnd = currentTime.plusMinutes(schedule.getSlotDurationMinutes());

            // Skip past slots for today's date
            if (isToday && slotStart.isBefore(currentTimeNow)) {
                currentTime = slotEnd;
                continue;
            }

            boolean isBooked = bookedAppointments.stream()
                    .anyMatch(a -> a.getAppointmentTime().equals(slotStart));

            slots.add(TimeSlotDTO.builder()
                    .doctorId(doctorId)
                    .date(date)
                    .startTime(slotStart)
                    .endTime(slotEnd)
                    .isAvailable(!isBooked)
                    .build());

            currentTime = slotEnd;
        }

        return slots;
    }

    // Get appointment by ID
    public AppointmentDTO getAppointmentById(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return mapToDTO(appointment);
    }

    // Get patient's appointments
    public List<AppointmentDTO> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescAppointmentTimeDesc(patientId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get doctor's appointments
    public List<AppointmentDTO> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateAscAppointmentTimeAsc(doctorId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get doctor's today appointments
    public List<AppointmentDTO> getDoctorTodayAppointments(Long doctorId) {
        return appointmentRepository.findTodaysAppointments(doctorId, LocalDate.now())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get all appointments
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get appointments by date
    public List<AppointmentDTO> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Delete all appointments for a patient (cascade deletion)
    @Transactional
    public void deleteByPatientId(Long patientId) {
        log.info("Cascade deleting all appointments for patient ID: {}", patientId);
        appointmentRepository.deleteByPatientId(patientId);
    }

    // Delete all appointments and schedules for a doctor (cascade deletion)
    @Transactional
    public void deleteByDoctorId(Long doctorId) {
        log.info("Cascade deleting all appointments and schedules for doctor ID: {}", doctorId);
        appointmentRepository.deleteByDoctorId(doctorId);
        doctorScheduleRepository.deleteByDoctorId(doctorId);
    }

    // Map entity to DTO
    private AppointmentDTO mapToDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .appointmentId(appointment.getAppointmentId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .appointmentStatus(appointment.getAppointmentStatus())
                .reasonForVisit(appointment.getReasonForVisit())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}

