package com.storeapp.activity.dto;

import com.storeapp.activity.entity.LocationProvider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * DTO per la risposta con i dettagli di un'attività
 */
public class ActivityDto {

    public Long id;
    public Long groupId;  // CHANGED: was itineraryId (direct link to group now)
    public String name;
    public String description;
    public LocalDate scheduledDate;
    public LocalTime startTime;
    public LocalTime endTime;

    // Location
    public String locationName;
    public String locationAddress;
    public BigDecimal locationLat;
    public BigDecimal locationLng;
    public String locationPlaceId;
    public LocationProvider locationProvider;
    public Map<String, Object> locationMetadata;

    public Boolean isCompleted;
    public Integer displayOrder;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    // Statistiche partecipanti
    public Long confirmedCount;
    public Long maybeCount;
    public Long declinedCount;

    public List<ActivityParticipantDto> participants;
    public List<ActivityExpenseDto> expenses;

    // Getter/Setter methods for collections
    public List<ActivityParticipantDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ActivityParticipantDto> participants) {
        this.participants = participants;
    }

    public List<ActivityExpenseDto> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ActivityExpenseDto> expenses) {
        this.expenses = expenses;
    }
}
