package com.storeapp.activity.repository;

import com.storeapp.activity.dto.ActivityCalendarDto;
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
    public List<ActivityCalendarDto> findByGroupAndDateRange(Long groupId, LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT 
                id, group_id, title, description,
                start_time, end_time,
                day_of_week, activity_date,
                location_name, location_lat, location_lng,
                is_completed, calendar_status,
                confirmed_count, maybe_count, declined_count, total_members,
                creator_name, creator_avatar
            FROM activity_calendar
            WHERE group_id = :groupId
            AND activity_date BETWEEN :startDate AND :endDate
            ORDER BY activity_date, start_time
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("groupId", groupId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all activities for a group (no date filtering)
     */
    @SuppressWarnings("unchecked")
    public List<ActivityCalendarDto> findByGroup(Long groupId) {
        String sql = """
            SELECT 
                id, group_id, title, description,
                start_time, end_time,
                day_of_week, activity_date,
                location_name, location_lat, location_lng,
                is_completed, calendar_status,
                confirmed_count, maybe_count, declined_count, total_members,
                creator_name, creator_avatar
            FROM activity_calendar
            WHERE group_id = :groupId
            ORDER BY activity_date, start_time
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("groupId", groupId);

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
        dto.groupId = ((Number) row[i++]).longValue();  // CHANGED: no more itinerary_id
        dto.title = (String) row[i++];
        dto.description = (String) row[i++];
        
        // start and end are LocalDateTime in DTO
        dto.start = row[i++] != null ? ((java.sql.Time) row[i-1]).toLocalTime().atDate(LocalDate.now()) : null;
        dto.end = row[i++] != null ? ((java.sql.Time) row[i-1]).toLocalTime().atDate(LocalDate.now()) : null;
        
        // day_of_week is String in the view
        String dayOfWeekStr = (String) row[i++];
        if (dayOfWeekStr != null) {
            dayOfWeekStr = dayOfWeekStr.trim().toUpperCase();
            dto.dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr);
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
