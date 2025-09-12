package com.ddbs.choroid_auth_service.exception;

/**
 * Exception thrown when user provides invalid credentials
 */
public class InvalidCredentialsException extends AuthException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
