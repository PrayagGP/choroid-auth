package com.ddbs.choroid_auth_service.exception;

/**
 * Base exception class for authentication-related errors
 */
public class AuthException extends RuntimeException {
    
    public AuthException(String message) {
        super(message);
    }
    
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
