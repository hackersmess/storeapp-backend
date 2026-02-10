package com.storeapp.auth.exception;

import com.storeapp.shared.exception.BusinessException;

/**
 * Eccezione lanciata quando si tenta di registrare un utente con email già esistente.
 */
public class UserAlreadyExistsException extends BusinessException {

    public UserAlreadyExistsException(String message) {
        super(message, "USER_ALREADY_EXISTS", 409);
    }
}
