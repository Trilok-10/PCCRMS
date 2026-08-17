package com.genc.auth_service.exception;

import com.genc.auth_service.dto.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "An error occurred while processing your request";
        
        String errorMessage = ex.getMessage();
        if (errorMessage != null) {
            errorMessage = errorMessage.toLowerCase();
            
            // Check for duplicate phone number
            if (errorMessage.contains("phone") || errorMessage.contains("uk_du5v5sr43g5bfnji4vb8hg5s3")) {
                message = "Phone number already exists";
            }
            // Check for duplicate email
            else if (errorMessage.contains("email") || errorMessage.contains("uk_")) {
                message = "Email already exists";
            }
            // Check for duplicate MRN or other unique constraints
            else if (errorMessage.contains("mrn")) {
                message = "MRN already exists";
            }
            // Generic duplicate entry
            else if (errorMessage.contains("duplicate entry")) {
                message = "This record already exists";
            }
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred"));
    }
}

