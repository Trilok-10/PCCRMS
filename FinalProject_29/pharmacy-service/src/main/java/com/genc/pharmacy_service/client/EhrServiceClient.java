package com.genc.pharmacy_service.client;

import com.genc.pharmacy_service.dto.ApiResponse;
import com.genc.pharmacy_service.dto.ClinicalRecordDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ehr-service")
public interface EhrServiceClient {

    @PutMapping("/api/ehr/encounters/{recordId}/notes")
    ApiResponse<ClinicalRecordDTO> updateEncounterNotes(
            @PathVariable("recordId") Long recordId,
            @RequestBody String clinicalNotes);
}
