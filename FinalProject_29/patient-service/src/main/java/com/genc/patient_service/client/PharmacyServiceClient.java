package com.genc.patient_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pharmacy-service")
public interface PharmacyServiceClient {

    @DeleteMapping("/api/pharmacy/prescriptions/patient/{patientId}")
    void deleteByPatientId(@PathVariable("patientId") Long patientId);
}
