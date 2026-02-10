package com.storeapp.auth.exception;

/**
 * Eccezione lanciata quando le credenziali di login sono invalide.
 */
public class InvalidCredentialsException extends AuthException {
    
    public InvalidCredentialsException(String message) {
        super(message, "INVALID_CREDENTIALS");
    }
}
