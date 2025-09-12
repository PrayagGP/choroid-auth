package com.ddbs.choroid_auth_service.repository;

import com.ddbs.choroid_auth_service.model.Credentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialsRepository extends JpaRepository<Credentials, String> {
    
    /**
     * Find credentials by username (inherited from JpaRepository as findById)
     * @param username the username to search for
     * @return Optional containing the credentials if found
     */
    // findById(String username) is inherited from JpaRepository
    
    /**
     * Check if username exists (inherited from JpaRepository as existsById)
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    // existsById(String username) is inherited from JpaRepository
}
