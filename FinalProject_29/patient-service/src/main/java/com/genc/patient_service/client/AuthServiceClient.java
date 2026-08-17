package com.genc.patient_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @DeleteMapping("/api/auth/users/{userId}")
    void deleteUser(@PathVariable("userId") Long userId);
}
