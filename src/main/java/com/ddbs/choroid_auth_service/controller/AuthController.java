package com.ddbs.choroid_auth_service.controller;

import com.ddbs.choroid_auth_service.dto.AuthResponse;
import com.ddbs.choroid_auth_service.dto.LoginRequest;
import com.ddbs.choroid_auth_service.dto.SignupRequest;
import com.ddbs.choroid_auth_service.dto.UpdatePasswordRequest;
import com.ddbs.choroid_auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private final AuthService authService;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * User login endpoint
     * @param loginRequest Login credentials
     * @return Authentication response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for username: {}", loginRequest.getUsername());
        
        String token = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        String refreshToken = authService.generateRefreshToken(loginRequest.getUsername());
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(jwtExpiration / 1000);
        
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .username(loginRequest.getUsername())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .message("Login successful")
                .build();

        log.info("Login successful for username: {}", loginRequest.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * User signup endpoint
     * @param signupRequest Signup credentials
     * @return Success message
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        log.info("Signup request received for username: {}", signupRequest.getUsername());
        
        authService.signup(signupRequest.getUsername(), signupRequest.getPassword());
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("username", signupRequest.getUsername());
        response.put("timestamp", LocalDateTime.now().toString());
        
        log.info("Signup successful for username: {}", signupRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Token validation endpoint
     * @param token JWT token to validate
     * @return Validation result
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam String token) {
        log.info("Token validation request received");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String username = authService.extractUsername(token);
            if (username != null) {
                boolean isValid = authService.validateToken(token, username);
                response.put("valid", isValid);
                response.put("username", isValid ? username : null);
                response.put("message", isValid ? "Token is valid" : "Token is invalid or expired");
            } else {
                response.put("valid", false);
                response.put("username", null);
                response.put("message", "Invalid token format");
            }
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            response.put("valid", false);
            response.put("username", null);
            response.put("message", "Token validation failed");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update password endpoint
     * @param updatePasswordRequest Update password credentials
     * @return Success message
     */
    @PostMapping("/update-password")
    public ResponseEntity<Map<String, String>> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        log.info("Password update request received for username: {}", updatePasswordRequest.getUsername());
        
        // Validate that new password and confirm password match
        if (!updatePasswordRequest.getNewPassword().equals(updatePasswordRequest.getConfirmPassword())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Password confirmation mismatch");
            errorResponse.put("message", "New password and confirm password do not match");
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        authService.updatePassword(
                updatePasswordRequest.getUsername(),
                updatePasswordRequest.getCurrentPassword(),
                updatePasswordRequest.getNewPassword()
        );
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully");
        response.put("username", updatePasswordRequest.getUsername());
        response.put("timestamp", LocalDateTime.now().toString());
        
        log.info("Password update successful for username: {}", updatePasswordRequest.getUsername());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check endpoint
     * @return Service status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Choroid Authentication Service");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
}
