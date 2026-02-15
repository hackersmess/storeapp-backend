package com.storeapp.activity.controller;

import com.storeapp.activity.dto.ActivityCalendarDto;
import com.storeapp.activity.service.ActivityCalendarService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Activity Calendar views
 * Base path: /api/groups/{groupId}/calendar
 */
@Path("/api/groups/{groupId}/calendar")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class ActivityCalendarController {

    @Inject
    ActivityCalendarService calendarService;

    @Inject
    JsonWebToken jwt;

    private Long getCurrentUserId() {
        return Long.parseLong(jwt.getSubject());
    }

    /**
     * Get all activities in calendar format
     * GET /api/groups/{groupId}/calendar
     */
    @GET
    public Response getAllActivities(@PathParam("groupId") Long groupId) {
        Long userId = getCurrentUserId();
        List<ActivityCalendarDto> activities = calendarService.getAllActivitiesCalendar(groupId, userId);
        
        return Response.ok(activities).build();
    }

    /**
     * Get activities within a date range
     * GET /api/groups/{groupId}/calendar/range?start=2024-01-01&end=2024-01-31
     */
    @GET
    @Path("/range")
    public Response getCalendarByRange(
            @PathParam("groupId") Long groupId,
            @QueryParam("start") String startDate,
            @QueryParam("end") String endDate) {
        
        Long userId = getCurrentUserId();
        
        // Parse dates
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        List<ActivityCalendarDto> activities = calendarService.getCalendarView(
                groupId, start, end, userId);
        
        return Response.ok(activities).build();
    }

    /**
     * Get activities for a specific month
     * GET /api/groups/{groupId}/calendar/month/2024/1
     */
    @GET
    @Path("/month/{year}/{month}")
    public Response getCalendarByMonth(
            @PathParam("groupId") Long groupId,
            @PathParam("year") int year,
            @PathParam("month") int month) {
        
        Long userId = getCurrentUserId();
        List<ActivityCalendarDto> activities = calendarService.getCalendarByMonth(
                groupId, year, month, userId);
        
        return Response.ok(activities).build();
    }

    /**
     * Get activities for a specific week
     * GET /api/groups/{groupId}/calendar/week?start=2024-01-01
     */
    @GET
    @Path("/week")
    public Response getCalendarByWeek(
            @PathParam("groupId") Long groupId,
            @QueryParam("start") String weekStart) {
        
        Long userId = getCurrentUserId();
        
        // Parse week start date
        LocalDate start = LocalDate.parse(weekStart);
        
        // Adjust to Monday if not already (ISO-8601 week starts on Monday)
        if (start.getDayOfWeek() != DayOfWeek.MONDAY) {
            start = start.with(DayOfWeek.MONDAY);
        }
        
        List<ActivityCalendarDto> activities = calendarService.getCalendarByWeek(
                groupId, start, userId);
        
        return Response.ok(activities).build();
    }
}
