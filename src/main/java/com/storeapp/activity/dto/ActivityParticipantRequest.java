package com.storeapp.activity.dto;

import com.storeapp.activity.entity.ParticipantStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO per aggiungere/aggiornare un partecipante
 */
public class ActivityParticipantRequest {

    @NotNull(message = "L'ID del membro del gruppo è obbligatorio")
    public Long groupMemberId;

    public ParticipantStatus status = ParticipantStatus.CONFIRMED;

    public String notes;
}
