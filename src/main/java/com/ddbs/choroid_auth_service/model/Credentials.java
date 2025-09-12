package com.ddbs.choroid_auth_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {
    
    @Id
    @Column(length = 50)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Password is required")
    private String password;
}

