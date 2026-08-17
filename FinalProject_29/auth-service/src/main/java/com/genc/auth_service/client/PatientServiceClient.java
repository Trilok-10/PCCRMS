package com.genc.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service")
public interface PatientServiceClient {

    @DeleteMapping("/api/patients/user/{userId}")
    void deletePatientByUserId(@PathVariable("userId") Long userId);
}
