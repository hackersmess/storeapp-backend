package com.storeapp.activity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Generic Activity Request DTO - DEPRECATED in favor of EventRequest/TripRequest
 * This is kept for backward compatibility with old API endpoints
 * 
 * For new code, use:
 * - EventRequest for single-location activities
 * - TripRequest for travel activities
 * 
 * The activityType discriminator determines which subtype to create
 */
@Deprecated
public class ActivityRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 255, message = "Il nome non può superare 255 caratteri")
    public String name;

    public String description;

    @NotNull(message = "La data di inizio è obbligatoria")
    public LocalDate startDate;

    public LocalDate endDate; // For multi-day activities

    public LocalTime startTime;

    public LocalTime endTime;

    // Activity type discriminator ('EVENT' or 'TRIP')
    @NotNull(message = "Il tipo di attività è obbligatorio")
    public String activityType = "EVENT";

    public Boolean isCompleted = false;

    public Integer displayOrder = 0;

    public BigDecimal totalCost;

    // DEPRECATED: Use scheduledDate as alias for startDate (backward compatibility)
    @Deprecated
    public LocalDate scheduledDate;

    /**
     * Get effective start date (handles backward compatibility)
     */
    public LocalDate getEffectiveStartDate() {
        return startDate != null ? startDate : scheduledDate;
    }
}
