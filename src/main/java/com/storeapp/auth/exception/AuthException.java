package com.storeapp.auth.exception;

import com.storeapp.shared.exception.BusinessException;

/**
 * Eccezione base per errori di autenticazione.
 */
public class AuthException extends BusinessException {

    public AuthException(String message, String errorCode) {
        super(message, errorCode, 401);
    }

    public AuthException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, 401, cause);
    }
}
