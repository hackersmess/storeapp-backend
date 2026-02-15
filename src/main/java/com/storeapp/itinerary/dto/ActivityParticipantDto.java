package com.storeapp.itinerary.dto;

import com.storeapp.group.dto.GroupMemberDto;
import com.storeapp.itinerary.entity.ParticipantStatus;

import java.time.LocalDateTime;

/**
 * DTO per la risposta con i dettagli di un partecipante
 */
public class ActivityParticipantDto {

    public Long id;
    public Long activityId;
    public GroupMemberDto groupMember;
    public ParticipantStatus status;
    public String notes;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
