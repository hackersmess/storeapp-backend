package com.storeapp.itinerary.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO per la risposta con i dettagli completi di un itinerario
 * Le date sono ereditate dal gruppo (group.vacationStartDate, group.vacationEndDate)
 */
public class ItineraryDto {

    public Long id;
    public Long groupId;
    public String name;
    public String description;
    // Le date sono disponibili dal gruppo, non servono qui
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long activityCount;
    public Long completedActivitiesCount;
    public List<ActivityDto> activities;
}
