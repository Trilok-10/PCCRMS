package com.genc.patient_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 3, message = "Full name must be at least 3 characters")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Full name must contain only letters and spaces")
    private String fullName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    private String gender;

    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be exactly 10 digits")
    private String contactNumber;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 10, message = "Address must be at least 10 characters")
    private String address;

    @Pattern(regexp = "^[A-Za-z\\s]*$", message = "Emergency contact name must contain only letters and spaces")
    @Size(min = 3, message = "Emergency contact name must be at least 3 characters")
    private String emergencyContactName;

    @Pattern(regexp = "^$|^[0-9]{10}$", message = "Emergency phone must be exactly 10 digits")
    private String emergencyPhone;

    private String bloodGroup;

    private String allergies;

    // For linking patient to user account (optional)
    private Long userId;
}

