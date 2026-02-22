package com.storeapp.activity.dto;

import com.storeapp.activity.entity.Location;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Generic Activity Response DTO - DEPRECATED in favor of EventDto/TripDto
 * This is kept for backward compatibility with old API endpoints
 * 
 * For new code, use:
 * - EventDto for Event activities
 * - TripDto for Trip activities
 * 
 * The activityType field indicates which subtype this represents
 */
@Deprecated
public class ActivityDto {

    public Long id;
    public Long groupId;
    public String name;
    public String description;

    // Date/time fields (new V5 schema)
    public LocalDate startDate;
    public LocalDate endDate;
    public LocalTime startTime;
    public LocalTime endTime;

    // Activity type discriminator ('EVENT' or 'TRIP')
    public String activityType;

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

    // Relations
    public List<ActivityParticipantDto> participants;
    public List<ActivityExpenseDto> expenses;

    // DEPRECATED: Backward compatibility field (use startDate instead)
    @Deprecated
    public LocalDate scheduledDate;

    // Event-specific fields
    public String category; // EventCategory as string
    public Location location;
    public String bookingUrl;
    public String bookingReference;
    public LocalTime reservationTime;

    // Trip-specific fields
    public String transportMode; // TransportMode as string
    public Location origin;
    public Location destination;
    public LocalTime departureTime;
    public LocalTime arrivalTime;

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

    /**
     * Helper method for multi-day activities
     */
    public boolean isMultiDay() {
        return endDate != null && !endDate.equals(startDate);
    }
}
