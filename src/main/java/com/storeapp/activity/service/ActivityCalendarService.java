package com.storeapp.activity.service;

import com.storeapp.group.entity.Group;
import com.storeapp.group.repository.GroupRepository;
import com.storeapp.activity.dto.ActivityCalendarDto;
import com.storeapp.activity.repository.ActivityCalendarRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service for activity calendar operations
 * Provides calendar views with different time ranges
 */
@ApplicationScoped
public class ActivityCalendarService {

    @Inject
    ActivityCalendarRepository calendarRepository;

    @Inject
    GroupRepository groupRepository;  // CHANGED: was ItineraryRepository

    /**
     * Get calendar view for a date range
     */
    public List<ActivityCalendarDto> getCalendarView(Long groupId, LocalDate startDate, LocalDate endDate, Long userId) {
        // Verify group exists and user has access
        verifyAccess(groupId, userId);

        return calendarRepository.findByGroupAndDateRange(groupId, startDate, endDate, userId);
    }

    /**
     * Get calendar view for a specific month
     */
    public List<ActivityCalendarDto> getCalendarByMonth(Long groupId, int year, int month, Long userId) {
        verifyAccess(groupId, userId);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return calendarRepository.findByGroupAndDateRange(groupId, startDate, endDate, userId);
    }

    /**
     * Get calendar view for a week starting from a specific date
     */
    public List<ActivityCalendarDto> getCalendarByWeek(Long groupId, LocalDate weekStart, Long userId) {
        verifyAccess(groupId, userId);

        LocalDate weekEnd = weekStart.plus(6, ChronoUnit.DAYS);
        return calendarRepository.findByGroupAndDateRange(groupId, weekStart, weekEnd, userId);
    }

    /**
     * Get all activities for a group (no date filtering)
     */
    public List<ActivityCalendarDto> getAllActivitiesCalendar(Long groupId, Long userId) {
        verifyAccess(groupId, userId);

        return calendarRepository.findByGroup(groupId, userId);
    }

    /**
     * Verify user has access to group
     */
    private void verifyAccess(Long groupId, Long userId) {
        Group group = groupRepository.findByIdWithMembers(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupId));

        if (!group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }
    }
}
