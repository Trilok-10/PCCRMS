package com.genc.appointment_service.service;

import com.genc.appointment_service.dto.DoctorScheduleRequest;
import com.genc.appointment_service.model.DoctorSchedule;
import com.genc.appointment_service.repository.DoctorScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorScheduleService {

    private final DoctorScheduleRepository doctorScheduleRepository;

    // Create doctor schedule
    @Transactional
    public DoctorSchedule createSchedule(DoctorScheduleRequest request) {
        log.info("Creating schedule for doctor {} on {}", request.getDoctorId(), request.getDayOfWeek());

        if (doctorScheduleRepository.existsByDoctorIdAndDayOfWeek(request.getDoctorId(), request.getDayOfWeek())) {
            throw new RuntimeException("Schedule already exists for this day. Use update instead.");
        }

        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctorId(request.getDoctorId())
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .slotDurationMinutes(request.getSlotDurationMinutes())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .build();

        return doctorScheduleRepository.save(schedule);
    }

    // Update doctor schedule
    @Transactional
    public DoctorSchedule updateSchedule(Long scheduleId, DoctorScheduleRequest request) {
        DoctorSchedule schedule = doctorScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setSlotDurationMinutes(request.getSlotDurationMinutes());
        schedule.setIsAvailable(request.getIsAvailable());

        return doctorScheduleRepository.save(schedule);
    }

    // Get doctor's schedule
    public List<DoctorSchedule> getDoctorSchedule(Long doctorId) {
        return doctorScheduleRepository.findByDoctorId(doctorId);
    }

    // Get schedule for specific day
    public DoctorSchedule getScheduleForDay(Long doctorId, DayOfWeek dayOfWeek) {
        return doctorScheduleRepository.findByDoctorIdAndDayOfWeek(doctorId, dayOfWeek)
                .orElseThrow(() -> new RuntimeException("No schedule found for " + dayOfWeek));
    }

    // Toggle availability
    @Transactional
    public DoctorSchedule toggleAvailability(Long scheduleId) {
        DoctorSchedule schedule = doctorScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        schedule.setIsAvailable(!schedule.getIsAvailable());
        return doctorScheduleRepository.save(schedule);
    }

    // Delete schedule
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        if (!doctorScheduleRepository.existsById(scheduleId)) {
            throw new RuntimeException("Schedule not found");
        }
        doctorScheduleRepository.deleteById(scheduleId);
    }
}

