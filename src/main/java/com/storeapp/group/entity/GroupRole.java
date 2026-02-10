package com.storeapp.group.entity;

/**
 * Ruoli possibili per i membri di un gruppo
 */
public enum GroupRole {
    /**
     * Amministratore: può gestire il gruppo, invitare/rimuovere membri, modificare impostazioni
     */
    ADMIN,
    
    /**
     * Membro standard: può partecipare alle attività del gruppo
     */
    MEMBER
}
