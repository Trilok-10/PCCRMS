package com.genc.patient_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ehr-service")
public interface EhrServiceClient {

    @DeleteMapping("/api/ehr/patients/{patientId}/records")
    void deleteByPatientId(@PathVariable("patientId") Long patientId);
}
