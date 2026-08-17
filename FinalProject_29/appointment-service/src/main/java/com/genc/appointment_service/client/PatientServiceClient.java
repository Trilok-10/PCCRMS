package com.genc.appointment_service.client;

import com.genc.appointment_service.dto.ApiResponse;
import com.genc.appointment_service.dto.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service")
public interface PatientServiceClient {

    @GetMapping("/api/patients/{patientId}")
    ApiResponse<PatientDTO> getPatientById(@PathVariable("patientId") Long patientId);
}
