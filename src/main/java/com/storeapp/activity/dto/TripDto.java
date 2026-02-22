package com.storeapp.activity.dto;

import com.storeapp.activity.entity.TransportMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Response DTO for Trip activities
 * Trips are travel activities with origin and destination (flights, trains, car trips, etc.)
 */
public class TripDto {

    public Long id;
    public Long groupId;
    public String name;
    public String description;

    // Date/time fields
    public LocalDate startDate;
    public LocalDate endDate;
    public LocalTime startTime;
    public LocalTime endTime;

    // Activity type discriminator
    public String activityType = "TRIP";

    // Locations (embedded)
    public LocationDto origin;
    public LocationDto destination;

    // Trip-specific fields
    public TransportMode transportMode;
    public LocalTime departureTime;
    public LocalTime arrivalTime;
    public String bookingReference;

    // Common fields
    public Boolean isCompleted;
    public Integer displayOrder;
    public BigDecimal totalCost;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long createdBy;

    // Participant statistics
    public Long confirmedCount;
    public Long maybeCount;
    public Long declinedCount;

    // Relations (optional, loaded on demand)
    public List<ActivityParticipantDto> participants;
    public List<ActivityExpenseDto> expenses;

    // Helper methods
    public boolean isMultiDay() {
        return endDate != null && !endDate.equals(startDate);
    }

    public int getDurationDays() {
        if (endDate == null || startDate == null) {
            return 1;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public String getRoute() {
        String originName = (origin != null) ? origin.getDisplayName() : "?";
        String destName = (destination != null) ? destination.getDisplayName() : "?";
        return originName + " → " + destName;
    }

    // Semantic aliases for Trip (departure/arrival instead of start/end)
    // These map to the base startDate/endDate fields for better UX
    
    /**
     * Get departure date (alias for startDate)
     */
    public LocalDate getDepartureDate() {
        return this.startDate;
    }

    /**
     * Set departure date (alias for startDate)
     */
    public void setDepartureDate(LocalDate departureDate) {
        this.startDate = departureDate;
    }

    /**
     * Get arrival date (alias for endDate)
     */
    public LocalDate getArrivalDate() {
        return this.endDate;
    }

    /**
     * Set arrival date (alias for endDate)
     */
    public void setArrivalDate(LocalDate arrivalDate) {
        this.endDate = arrivalDate;
    }
}
