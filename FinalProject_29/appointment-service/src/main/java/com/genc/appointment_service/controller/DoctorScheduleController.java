package com.genc.appointment_service.controller;

import com.genc.appointment_service.dto.ApiResponse;
import com.genc.appointment_service.dto.DoctorScheduleRequest;
import com.genc.appointment_service.model.DoctorSchedule;
import com.genc.appointment_service.service.DoctorScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/api/appointments/schedules")
@RequiredArgsConstructor
@Slf4j
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    // Create doctor schedule
    @PostMapping
    public ResponseEntity<ApiResponse<DoctorSchedule>> createSchedule(
            @Valid @RequestBody DoctorScheduleRequest request) {
        DoctorSchedule schedule = doctorScheduleService.createSchedule(request);
        return ResponseEntity.ok(ApiResponse.success("Schedule created successfully", schedule));
    }

    // Update doctor schedule
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<DoctorSchedule>> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody DoctorScheduleRequest request) {
        DoctorSchedule schedule = doctorScheduleService.updateSchedule(scheduleId, request);
        return ResponseEntity.ok(ApiResponse.success("Schedule updated successfully", schedule));
    }

    // Get doctor's schedule
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<DoctorSchedule>>> getDoctorSchedule(@PathVariable Long doctorId) {
        List<DoctorSchedule> schedules = doctorScheduleService.getDoctorSchedule(doctorId);
        return ResponseEntity.ok(ApiResponse.success(schedules));
    }

    // Get schedule for specific day
    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}")
    public ResponseEntity<ApiResponse<DoctorSchedule>> getScheduleForDay(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek) {
        DoctorSchedule schedule = doctorScheduleService.getScheduleForDay(doctorId, dayOfWeek);
        return ResponseEntity.ok(ApiResponse.success(schedule));
    }

    // Toggle availability
    @PutMapping("/{scheduleId}/toggle")
    public ResponseEntity<ApiResponse<DoctorSchedule>> toggleAvailability(@PathVariable Long scheduleId) {
        DoctorSchedule schedule = doctorScheduleService.toggleAvailability(scheduleId);
        return ResponseEntity.ok(ApiResponse.success("Availability toggled", schedule));
    }

    // Delete schedule
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<String>> deleteSchedule(@PathVariable Long scheduleId) {
        doctorScheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(ApiResponse.success("Schedule deleted successfully", null));
    }
}
