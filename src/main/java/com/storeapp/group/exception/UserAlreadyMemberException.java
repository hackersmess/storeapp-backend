package com.storeapp.group.exception;

import com.storeapp.shared.exception.BusinessException;

/**
 * Eccezione lanciata quando un utente è già membro di un gruppo
 */
public class UserAlreadyMemberException extends BusinessException {

    public UserAlreadyMemberException(String email) {
        super(
            "L'utente " + email + " è già membro del gruppo",
            "USER_ALREADY_MEMBER",
            409
        );
    }
}
