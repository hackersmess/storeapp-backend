package com.storeapp.group.exception;

import com.storeapp.shared.exception.BusinessException;

/**
 * Eccezione lanciata quando un utente non ha i permessi per un'operazione
 */
public class InsufficientPermissionsException extends BusinessException {

    public InsufficientPermissionsException(String message) {
        super(message, "INSUFFICIENT_PERMISSIONS", 403);
    }
    
    public static InsufficientPermissionsException adminRequired() {
        return new InsufficientPermissionsException("Solo gli amministratori possono eseguire questa operazione");
    }
    
    public static InsufficientPermissionsException creatorRequired() {
        return new InsufficientPermissionsException("Solo il creatore del gruppo può eseguire questa operazione");
    }

    public static InsufficientPermissionsException memberRequired() {
        return new InsufficientPermissionsException("Devi essere membro del gruppo per eseguire questa operazione");
    }
}
