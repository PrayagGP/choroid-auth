package com.ddbs.choroid_auth_service.service;

import com.ddbs.choroid_auth_service.exception.InvalidCredentialsException;
import com.ddbs.choroid_auth_service.exception.UserAlreadyExistsException;
import com.ddbs.choroid_auth_service.model.Credentials;
import com.ddbs.choroid_auth_service.repository.CredentialsRepository;
import com.ddbs.choroid_auth_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Authentication service for user login, registration, and token management
 * Uses JDBC for database operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final CredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Authenticate user with username and password
     * @param username the username
     * @param password the password
     * @return JWT token if authentication successful
     * @throws InvalidCredentialsException if credentials are invalid
     */
    public String authenticate(String username, String password) {
        log.info("Authentication attempt for username: {}", username);
        
        // Input validation
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            log.warn("Authentication failed: empty username or password");
            throw new InvalidCredentialsException("Username and password are required");
        }
        
        try {
            Optional<Credentials> credentialsOpt = credentialsRepository.findById(username);
            
            if (credentialsOpt.isEmpty()) {
                log.warn("Authentication failed: user not found: {}", username);
                throw new InvalidCredentialsException("Invalid username or password");
            }
            
            Credentials credentials = credentialsOpt.get();
            
            if (!passwordEncoder.matches(password, credentials.getPassword())) {
                log.warn("Authentication failed: invalid password for user: {}", username);
                throw new InvalidCredentialsException("Invalid username or password");
            }
            
            String token = jwtUtil.generateToken(username);
            log.info("Authentication successful for user: {}", username);
            
            return token;
            
        } catch (Exception e) {
            if (e instanceof InvalidCredentialsException) {
                throw e;
            }
            log.error("Unexpected error during authentication for user: {}", username, e);
            throw new InvalidCredentialsException("Authentication failed");
        }
    }
    
    /**
     * Register a new user
     * @param username the username
     * @param password the password
     * @throws UserAlreadyExistsException if username already exists
     */
    public void signup(String username, String password) {
        log.info("Signup attempt for username: {}", username);
        
        // Input validation
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            log.warn("Signup failed: empty username or password");
            throw new IllegalArgumentException("Username and password are required");
        }
        
        if (username.length() < 3 || username.length() > 50) {
            log.warn("Signup failed: invalid username length: {}", username);
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        
        if (password.length() < 6) {
            log.warn("Signup failed: password too short for user: {}", username);
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        
        try {
            // Check if username already exists
            if (credentialsRepository.existsById(username)) {
                log.warn("Signup failed: username already exists: {}", username);
                throw new UserAlreadyExistsException("Username '" + username + "' is already taken");
            }
            
            // Create new credentials
            Credentials credentials = new Credentials(
                    username, 
                    passwordEncoder.encode(password)
            );
            
            credentialsRepository.save(credentials);
            log.info("User registered successfully: {}", username);
            
        } catch (Exception e) {
            if (e instanceof UserAlreadyExistsException || e instanceof IllegalArgumentException) {
                throw e;
            }
            log.error("Unexpected error during signup for user: {}", username, e);
            throw new RuntimeException("Registration failed. Please try again.");
        }
    }
    
    /**
     * Generate refresh token for user
     * @param username the username
     * @return refresh token
     */
    public String generateRefreshToken(String username) {
        log.info("Generating refresh token for user: {}", username);
        return jwtUtil.generateRefreshToken(username);
    }
    
    /**
     * Validate JWT token
     * @param token the JWT token
     * @param username the username to validate against
     * @return true if token is valid
     */
    public boolean validateToken(String token, String username) {
        try {
            return jwtUtil.validateToken(token, username);
        } catch (Exception e) {
            log.warn("Token validation failed for user: {}", username, e);
            return false;
        }
    }
    
    /**
     * Extract username from JWT token
     * @param token the JWT token
     * @return username if token is valid
     */
    public String extractUsername(String token) {
        try {
            return jwtUtil.extractUsername(token);
        } catch (Exception e) {
            log.warn("Failed to extract username from token", e);
            return null;
        }
    }
}
