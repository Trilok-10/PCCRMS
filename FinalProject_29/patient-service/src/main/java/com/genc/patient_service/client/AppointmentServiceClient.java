package com.genc.patient_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "appointment-service")
public interface AppointmentServiceClient {

    @DeleteMapping("/api/appointments/patient/{patientId}")
    void deleteByPatientId(@PathVariable("patientId") Long patientId);
}
