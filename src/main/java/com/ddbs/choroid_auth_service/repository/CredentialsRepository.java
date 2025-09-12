package com.ddbs.choroid_auth_service.repository;

import com.ddbs.choroid_auth_service.model.Credentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * JDBC-based repository for Credentials operations
 * Replaces JPA implementation with pure JDBC
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class CredentialsRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Row mapper for converting ResultSet to Credentials object
     */
    private static final RowMapper<Credentials> CREDENTIALS_ROW_MAPPER = new RowMapper<Credentials>() {
        @Override
        public Credentials mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Credentials(
                    rs.getString("username"),
                    rs.getString("password")
            );
        }
    };
    
    /**
     * Find credentials by username
     * @param username the username to search for
     * @return Optional containing the credentials if found
     */
    public Optional<Credentials> findById(String username) {
        log.debug("Finding credentials for username: {}", username);
        
        String sql = "SELECT username, password FROM credentials WHERE username = ?";
        
        try {
            Credentials credentials = jdbcTemplate.queryForObject(sql, CREDENTIALS_ROW_MAPPER, username);
            log.debug("Credentials found for username: {}", username);
            return Optional.of(credentials);
        } catch (EmptyResultDataAccessException e) {
            log.debug("No credentials found for username: {}", username);
            return Optional.empty();
        }
    }
    
    /**
     * Check if username exists in the database
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean existsById(String username) {
        log.debug("Checking if username exists: {}", username);
        
        String sql = "SELECT COUNT(*) FROM credentials WHERE username = ?";
        
        try {
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
            boolean exists = count != null && count > 0;
            log.debug("Username {} exists: {}", username, exists);
            return exists;
        } catch (Exception e) {
            log.error("Error checking if username exists: {}", username, e);
            return false;
        }
    }
    
    /**
     * Save credentials to the database
     * Performs INSERT or UPDATE (upsert) operation
     * @param credentials the credentials to save
     */
    public void save(Credentials credentials) {
        log.debug("Saving credentials for username: {}", credentials.getUsername());
        
        String sql = "INSERT INTO credentials (username, password) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE password = VALUES(password)";
        
        try {
            int rowsAffected = jdbcTemplate.update(sql, 
                    credentials.getUsername(), 
                    credentials.getPassword());
            
            log.debug("Credentials saved for username: {}, rows affected: {}", 
                    credentials.getUsername(), rowsAffected);
        } catch (Exception e) {
            log.error("Error saving credentials for username: {}", credentials.getUsername(), e);
            throw new RuntimeException("Failed to save credentials", e);
        }
    }
    
    /**
     * Delete credentials by username
     * @param username the username to delete
     */
    public void deleteById(String username) {
        log.debug("Deleting credentials for username: {}", username);
        
        String sql = "DELETE FROM credentials WHERE username = ?";
        
        try {
            int rowsAffected = jdbcTemplate.update(sql, username);
            log.debug("Credentials deleted for username: {}, rows affected: {}", username, rowsAffected);
        } catch (Exception e) {
            log.error("Error deleting credentials for username: {}", username, e);
            throw new RuntimeException("Failed to delete credentials", e);
        }
    }
    
    /**
     * Count total number of credentials
     * @return total count of credentials
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM credentials";
        
        try {
            Long count = jdbcTemplate.queryForObject(sql, Long.class);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("Error counting credentials", e);
            return 0L;
        }
    }
}
