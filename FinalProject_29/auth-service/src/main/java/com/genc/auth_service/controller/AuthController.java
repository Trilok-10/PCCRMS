package com.genc.auth_service.controller;

import com.genc.auth_service.dto.*;
import com.genc.auth_service.model.Role;
import com.genc.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Public: Login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    // Public: Patient self-registration
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    // Public: Validate token
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestParam String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success("Token validation result", isValid));
    }

    // Admin: Create user with specific role
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody RegisterRequest request) {
        UserDTO user = authService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success("User created successfully", user));
    }

    // Admin: Get all users
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = authService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    // Get user by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO user = authService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    // Get users by role
    @GetMapping("/users/role/{role}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@PathVariable Role role) {
        List<UserDTO> users = authService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    // Admin: Deactivate user
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<ApiResponse<UserDTO>> deactivateUser(@PathVariable Long id) {
        UserDTO user = authService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated", user));
    }

    // Admin: Activate user
    @PutMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse<UserDTO>> activateUser(@PathVariable Long id) {
        UserDTO user = authService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User activated", user));
    }

    // Admin: Reset password
    @PutMapping("/users/{id}/reset-password")
    public ResponseEntity<ApiResponse<UserDTO>> resetPassword(
            @PathVariable Long id,
            @RequestParam String newPassword) {
        UserDTO user = authService.resetPassword(id, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Password reset successful", user));
    }

    // Admin: Delete user permanently
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}

