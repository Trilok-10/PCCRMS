package com.genc.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pharmacy-service")
public interface PharmacyServiceClient {

    @DeleteMapping("/api/pharmacy/prescriptions/doctor/{doctorId}")
    void deleteByDoctorId(@PathVariable("doctorId") Long doctorId);
}
