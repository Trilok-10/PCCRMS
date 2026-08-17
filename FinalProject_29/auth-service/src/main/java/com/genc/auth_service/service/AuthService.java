package com.genc.auth_service.service;

import com.genc.auth_service.dto.*;
import com.genc.auth_service.model.Role;
import com.genc.auth_service.model.User;
import com.genc.auth_service.repository.UserRepository;
import com.genc.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final com.genc.auth_service.client.AppointmentServiceClient appointmentServiceClient;
    private final com.genc.auth_service.client.EhrServiceClient ehrServiceClient;
    private final com.genc.auth_service.client.PharmacyServiceClient pharmacyServiceClient;
    private final com.genc.auth_service.client.PatientServiceClient patientServiceClient;

    // Login user and return JWT token
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + request.getEmail()));

        if (!user.getActive()) {
            throw new RuntimeException("User account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    // Register new user (for patient self-registration)
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new RuntimeException("Email already registered");
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty() 
                && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }

        User user = User.builder()
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(Role.PATIENT)  // Self-registration is always PATIENT
                .active(true)
                .build();

        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    // Admin creates a new user with specific role
    public UserDTO createUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new RuntimeException("Email already registered");
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty() 
                && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already registered");
        }

        if (request.getRole() == null) {
            throw new RuntimeException("Role is required for admin user creation");
        }

        User user = User.builder()
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(request.getRole())
                .active(true)
                .build();

        user = userRepository.save(user);
        return mapToDTO(user);
    }

    // Get all users
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get user by ID
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToDTO(user);
    }

    // Get users by role
    public List<UserDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Deactivate user
    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setActive(false);
        user = userRepository.save(user);
        return mapToDTO(user);
    }

    // Activate user
    public UserDTO activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setActive(true);
        user = userRepository.save(user);
        return mapToDTO(user);
    }

    // Reset password (admin)
    public UserDTO resetPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setPassword(passwordEncoder.encode(newPassword));
        user = userRepository.save(user);
        return mapToDTO(user);
    }

    // Delete user (admin) - permanently removes user
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Perform cascade deletion based on role
        if (user.getRole() == Role.DOCTOR) {
            try {
                appointmentServiceClient.deleteByDoctorId(id);
            } catch (Exception e) {
                // Log and ignore to prevent failure if service is down
                System.err.println("Failed to delete doctor appointments: " + e.getMessage());
            }
            try {
                ehrServiceClient.deleteByDoctorId(id);
            } catch (Exception e) {
                System.err.println("Failed to delete doctor clinical records: " + e.getMessage());
            }
            try {
                pharmacyServiceClient.deleteByDoctorId(id);
            } catch (Exception e) {
                System.err.println("Failed to delete doctor prescriptions: " + e.getMessage());
            }
        } else if (user.getRole() == Role.PATIENT) {
            try {
                patientServiceClient.deletePatientByUserId(id);
            } catch (Exception e) {
                System.err.println("Failed to delete patient records: " + e.getMessage());
            }
        }

        userRepository.delete(user);
    }

    // Validate token
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    // Map User entity to UserDTO
    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

