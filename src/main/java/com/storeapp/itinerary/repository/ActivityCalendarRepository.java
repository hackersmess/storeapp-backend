package com.storeapp.itinerary.repository;

import com.storeapp.itinerary.dto.ActivityCalendarDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for querying activity_calendar view
 */
@ApplicationScoped
public class ActivityCalendarRepository {

    @Inject
    EntityManager entityManager;

    /**
     * Get calendar view for a date range
     */
    @SuppressWarnings("unchecked")
    public List<ActivityCalendarDto> findByItineraryAndDateRange(Long itineraryId, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT 
                id, itinerary_id, group_id, title, description,
                start, end, start_time, end_time,
                day_of_week, activity_date,
                location_name, location_lat, location_lng,
                is_completed, calendar_status,
                confirmed_count, maybe_count, declined_count, total_members,
                created_by_name, created_by_avatar
            FROM activity_calendar
            WHERE itinerary_id = :itineraryId
            AND activity_date BETWEEN :startDate AND :endDate
            ORDER BY activity_date, start_time
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("itineraryId", itineraryId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all activities for an itinerary (no date filtering)
     */
    @SuppressWarnings("unchecked")
    public List<ActivityCalendarDto> findByItinerary(Long itineraryId) {
        String sql = """
            SELECT 
                id, itinerary_id, group_id, title, description,
                start, end, start_time, end_time,
                day_of_week, activity_date,
                location_name, location_lat, location_lng,
                is_completed, calendar_status,
                confirmed_count, maybe_count, declined_count, total_members,
                created_by_name, created_by_avatar
            FROM activity_calendar
            WHERE itinerary_id = :itineraryId
            ORDER BY activity_date, start_time
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("itineraryId", itineraryId);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Map database result to ActivityCalendarDto
     */
    private ActivityCalendarDto mapToDto(Object[] row) {
        ActivityCalendarDto dto = new ActivityCalendarDto();
        
        int i = 0;
        dto.id = ((Number) row[i++]).longValue();
        // Skip itinerary_id (not in DTO)
        i++;
        dto.groupId = ((Number) row[i++]).longValue();
        dto.title = (String) row[i++];
        dto.description = (String) row[i++];
        
        // start and end are LocalDateTime in DTO
        dto.start = row[i++] != null ? ((java.sql.Date) row[i-1]).toLocalDate().atStartOfDay() : null;
        dto.end = row[i++] != null ? ((java.sql.Timestamp) row[i-1]).toLocalDateTime() : null;
        
        // Skip start_time and end_time (not in DTO)
        i++; // start_time
        i++; // end_time
        
        // day_of_week is DayOfWeek enum in DTO (1=Monday, 7=Sunday in PostgreSQL)
        if (row[i++] != null) {
            int dow = ((Number) row[i-1]).intValue();
            // PostgreSQL: 0=Sunday, 1=Monday, ..., 6=Saturday
            // Java DayOfWeek: 1=Monday, ..., 7=Sunday
            dto.dayOfWeek = dow == 0 ? DayOfWeek.SUNDAY : DayOfWeek.of(dow);
        }
        
        dto.activityDate = row[i++] != null ? ((java.sql.Date) row[i-1]).toLocalDate() : null;
        
        dto.locationName = (String) row[i++];
        dto.locationLat = row[i++] != null ? ((Number) row[i-1]).doubleValue() : null;
        dto.locationLng = row[i++] != null ? ((Number) row[i-1]).doubleValue() : null;
        
        dto.isCompleted = (Boolean) row[i++];
        dto.calendarStatus = (String) row[i++];
        
        dto.confirmedCount = row[i++] != null ? ((Number) row[i-1]).longValue() : 0L;
        dto.maybeCount = row[i++] != null ? ((Number) row[i-1]).longValue() : 0L;
        dto.declinedCount = row[i++] != null ? ((Number) row[i-1]).longValue() : 0L;
        dto.totalMembers = row[i++] != null ? ((Number) row[i-1]).longValue() : 0L;
        
        dto.creatorName = (String) row[i++];
        dto.creatorAvatar = (String) row[i++];
        
        return dto;
    }
}
