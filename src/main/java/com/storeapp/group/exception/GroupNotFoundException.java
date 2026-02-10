package com.storeapp.group.exception;

import com.storeapp.shared.exception.BusinessException;

/**
 * Eccezione lanciata quando un gruppo non viene trovato
 */
public class GroupNotFoundException extends BusinessException {

    public GroupNotFoundException(Long groupId) {
        super(
            "Gruppo con ID " + groupId + " non trovato",
            "GROUP_NOT_FOUND",
            404
        );
    }
}
