package com.storeapp.group.exception;

import com.storeapp.shared.exception.BusinessException;

/**
 * Eccezione lanciata quando si tenta un'operazione non permessa
 */
public class InvalidOperationException extends BusinessException {

    public InvalidOperationException(String message) {
        super(message, "INVALID_OPERATION", 400);
    }
    
    public static InvalidOperationException lastAdminCannotLeave() {
        return new InvalidOperationException(
            "L'ultimo amministratore non può abbandonare il gruppo. " +
            "Nomina prima un altro amministratore o elimina il gruppo.");
    }
    
    public static InvalidOperationException cannotRemoveYourself() {
        return new InvalidOperationException("Non puoi rimuovere te stesso dal gruppo. Usa la funzione 'Abbandona gruppo'.");
    }
    
    public static InvalidOperationException maxMembersReached() {
        return new InvalidOperationException("Il gruppo ha raggiunto il numero massimo di membri (50)");
    }
}
