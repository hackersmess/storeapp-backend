package com.storeapp.itinerary.service;

import com.storeapp.itinerary.dto.ActivityCalendarDto;
import com.storeapp.itinerary.entity.Itinerary;
import com.storeapp.itinerary.exception.ItineraryNotFoundException;
import com.storeapp.itinerary.repository.ActivityCalendarRepository;
import com.storeapp.itinerary.repository.ItineraryRepository;
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
    ItineraryRepository itineraryRepository;

    /**
     * Get calendar view for a date range
     */
    public List<ActivityCalendarDto> getCalendarView(Long itineraryId, LocalDate startDate, LocalDate endDate, Long userId) {
        // Verify itinerary exists and user has access
        verifyAccess(itineraryId, userId);

        return calendarRepository.findByItineraryAndDateRange(itineraryId, startDate, endDate);
    }

    /**
     * Get calendar view for a specific month
     */
    public List<ActivityCalendarDto> getCalendarByMonth(Long itineraryId, int year, int month, Long userId) {
        verifyAccess(itineraryId, userId);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return calendarRepository.findByItineraryAndDateRange(itineraryId, startDate, endDate);
    }

    /**
     * Get calendar view for a week starting from a specific date
     */
    public List<ActivityCalendarDto> getCalendarByWeek(Long itineraryId, LocalDate weekStart, Long userId) {
        verifyAccess(itineraryId, userId);

        LocalDate weekEnd = weekStart.plus(6, ChronoUnit.DAYS);
        return calendarRepository.findByItineraryAndDateRange(itineraryId, weekStart, weekEnd);
    }

    /**
     * Get all activities for an itinerary (no date filtering)
     */
    public List<ActivityCalendarDto> getAllActivitiesCalendar(Long itineraryId, Long userId) {
        verifyAccess(itineraryId, userId);

        return calendarRepository.findByItinerary(itineraryId);
    }

    /**
     * Verify user has access to itinerary
     */
    private void verifyAccess(Long itineraryId, Long userId) {
        Itinerary itinerary = itineraryRepository.findByIdOptional(itineraryId)
                .orElseThrow(() -> new ItineraryNotFoundException(itineraryId));

        if (!itinerary.group.isMember(userId)) {
            throw new RuntimeException("User is not a member of this group");
        }
    }
}
