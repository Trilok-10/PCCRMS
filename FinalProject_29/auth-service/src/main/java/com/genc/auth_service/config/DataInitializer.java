package com.genc.auth_service.config;

import com.genc.auth_service.model.Role;
import com.genc.auth_service.model.User;
import com.genc.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default admin user if not exists
        if (!userRepository.existsByEmail("admin@hospital.com")) {
            User admin = User.builder()
                    .email("admin@hospital.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Admin")
                    .phone("1234567890")
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.info("Default admin user created: admin@hospital.com / admin123");
        }

        // Create sample doctor
        if (!userRepository.existsByEmail("doctor@hospital.com")) {
            User doctor = User.builder()
                    .email("doctor@hospital.com")
                    .password(passwordEncoder.encode("doctor123"))
                    .fullName("Dr. John Smith")
                    .phone("1234567891")
                    .role(Role.DOCTOR)
                    .active(true)
                    .build();
            userRepository.save(doctor);
            log.info("Sample doctor created: doctor@hospital.com / doctor123");
        }

        // Create sample receptionist
        if (!userRepository.existsByEmail("receptionist@hospital.com")) {
            User receptionist = User.builder()
                    .email("receptionist@hospital.com")
                    .password(passwordEncoder.encode("reception123"))
                    .fullName("Jane Doe")
                    .phone("1234567892")
                    .role(Role.RECEPTIONIST)
                    .active(true)
                    .build();
            userRepository.save(receptionist);
            log.info("Sample receptionist created: receptionist@hospital.com / reception123");
        }

        // Create sample pharmacist
        if (!userRepository.existsByEmail("pharmacist@hospital.com")) {
            User pharmacist = User.builder()
                    .email("pharmacist@hospital.com")
                    .password(passwordEncoder.encode("pharma123"))
                    .fullName("Mike Johnson")
                    .phone("1234567893")
                    .role(Role.PHARMACIST)
                    .active(true)
                    .build();
            userRepository.save(pharmacist);
            log.info("Sample pharmacist created: pharmacist@hospital.com / pharma123");
        }

        // Create sample billing officer
        if (!userRepository.existsByEmail("billing@hospital.com")) {
            User billingOfficer = User.builder()
                    .email("billing@hospital.com")
                    .password(passwordEncoder.encode("billing123"))
                    .fullName("Sarah Williams")
                    .phone("1234567894")
                    .role(Role.BILLING_OFFICER)
                    .active(true)
                    .build();
            userRepository.save(billingOfficer);
            log.info("Sample billing officer created: billing@hospital.com / billing123");
        }

        log.info("==============================================");
        log.info("DEFAULT USERS INITIALIZED:");
        log.info("Admin:        admin@hospital.com / admin123");
        log.info("Doctor:       doctor@hospital.com / doctor123");
        log.info("Receptionist: receptionist@hospital.com / reception123");
        log.info("Pharmacist:   pharmacist@hospital.com / pharma123");
        log.info("Billing:      billing@hospital.com / billing123");
        log.info("==============================================");
    }
}

