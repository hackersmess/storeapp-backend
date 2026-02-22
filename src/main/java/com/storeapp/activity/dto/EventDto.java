package com.storeapp.activity.dto;

import com.storeapp.activity.entity.EventCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Response DTO for Event activities
 * Events are single-location activities (restaurants, museums, hotels, etc.)
 */
public class EventDto {

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
    public String activityType = "EVENT";

    // Location (embedded)
    public LocationDto location;

    // Event-specific fields
    public EventCategory category;
    public String bookingUrl;
    public String bookingReference;
    public LocalTime reservationTime;

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
}
