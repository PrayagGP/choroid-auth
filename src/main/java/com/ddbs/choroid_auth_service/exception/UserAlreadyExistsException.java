package com.ddbs.choroid_auth_service.exception;

/**
 * Exception thrown when trying to create a user that already exists
 */
public class UserAlreadyExistsException extends AuthException {
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
    
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
