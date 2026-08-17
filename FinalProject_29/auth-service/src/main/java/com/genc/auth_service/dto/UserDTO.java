package com.genc.auth_service.dto;

import com.genc.auth_service.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;
}

