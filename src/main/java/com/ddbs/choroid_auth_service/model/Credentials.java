package com.ddbs.choroid_auth_service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Credentials model - Plain POJO for JDBC operations
 * Represents user credentials in the database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
}

