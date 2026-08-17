package com.genc.patient_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "billing-service")
public interface BillingServiceClient {

    @DeleteMapping("/api/billing/invoices/patient/{patientId}")
    void deleteByPatientId(@PathVariable("patientId") Long patientId);
}
