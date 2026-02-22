package com.storeapp.activity.controller;

import com.storeapp.activity.dto.*;
import com.storeapp.activity.entity.ParticipantStatus;
import com.storeapp.activity.service.ActivityService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

/**
 * REST Controller for Activity operations
 * Base path: /api/groups/{groupId}/activities
 */
@Path("/api/groups/{groupId}/activities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class ActivityController {

		@Inject
		ActivityService activityService;

    @Inject
    JsonWebToken jwt;

    private Long getCurrentUserId() {
        return Long.parseLong(jwt.getSubject());
    }

    // =====================================================
    // ACTIVITY CRUD OPERATIONS
    // =====================================================

    // ─────────────────────────────────────────────────────
    // TYPE-SPECIFIC ENDPOINTS (RECOMMENDED)
    // ─────────────────────────────────────────────────────

    /**
     * Create new Event activity
     * POST /api/groups/{groupId}/activities/events
     */
    @POST
    @Path("/events")
    public Response createEvent(
            @PathParam("groupId") Long groupId,
            @Valid EventRequest request) {
        
        Long userId = getCurrentUserId();
        EventDto event = activityService.createEvent(groupId, request, userId);
        
        return Response.status(Response.Status.CREATED)
                .entity(event)
                .build();
    }

    /**
     * Create new Trip activity
     * POST /api/groups/{groupId}/activities/trips
     */
    @POST
    @Path("/trips")
    public Response createTrip(
            @PathParam("groupId") Long groupId,
            @Valid TripRequest request) {
        
        Long userId = getCurrentUserId();
        TripDto trip = activityService.createTrip(groupId, request, userId);
        
        return Response.status(Response.Status.CREATED)
                .entity(trip)
                .build();
    }

    /**
     * Update Event activity
     * PUT /api/groups/{groupId}/activities/events/{activityId}
     */
    @PUT
    @Path("/events/{activityId}")
    public Response updateEvent(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId,
            @Valid EventRequest request) {
        
        Long userId = getCurrentUserId();
        EventDto event = activityService.updateEvent(activityId, request, userId);
        
        return Response.ok(event).build();
    }

    /**
     * Update Trip activity
     * PUT /api/groups/{groupId}/activities/trips/{activityId}
     */
    @PUT
    @Path("/trips/{activityId}")
    public Response updateTrip(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId,
            @Valid TripRequest request) {
        
        Long userId = getCurrentUserId();
        TripDto trip = activityService.updateTrip(activityId, request, userId);
        
        return Response.ok(trip).build();
    }

    // ─────────────────────────────────────────────────────
    // GENERIC ENDPOINTS (DEPRECATED - for backward compatibility)
    // ─────────────────────────────────────────────────────

    /**
     * Create new activity (Event or Trip)
     * POST /api/groups/{groupId}/activities
     * 
     * @deprecated Use /events or /trips endpoints instead
     */
    @Deprecated
    @POST
    public Response createActivity(
            @PathParam("groupId") Long groupId,
            @Valid ActivityRequest request) {
        
        throw new UnsupportedOperationException(
            "This endpoint is deprecated. Please use:\n" +
            "  - POST /api/groups/" + groupId + "/activities/events (for Event)\n" +
            "  - POST /api/groups/" + groupId + "/activities/trips (for Trip)"
        );
    }

    /**
     * Get all activities for a group
     * GET /api/groups/{groupId}/activities
     */
    @GET
    public Response getActivities(@PathParam("groupId") Long groupId) {
        Long userId = getCurrentUserId();
        List<ActivityDto> activities = activityService.getActivitiesByGroup(groupId, userId);
        
        return Response.ok(activities).build();
    }

    /**
     * Get single activity (basic info)
     * GET /api/groups/{groupId}/activities/{activityId}
     */
    @GET
    @Path("/{activityId}")
    public Response getActivity(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId) {
        
        Long userId = getCurrentUserId();
        ActivityDto activity = activityService.getActivity(activityId, userId);
        
        return Response.ok(activity).build();
    }

    /**
     * Get single activity with full details (participants + expenses)
     * GET /api/groups/{groupId}/activities/{activityId}/details
     */
    @GET
    @Path("/{activityId}/details")
    public Response getActivityWithDetails(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId) {
        
        Long userId = getCurrentUserId();
        ActivityDto activity = activityService.getActivityWithDetails(activityId, userId);
        
        return Response.ok(activity).build();
    }

    /**
     * Update activity (Event or Trip)
     * PUT /api/groups/{groupId}/activities/{activityId}
     */
    /**
     * Update activity (Event or Trip)
     * PUT /api/groups/{groupId}/activities/{activityId}
     * 
     * @deprecated Use PUT /events/{id} or PUT /trips/{id} endpoints instead
     */
    @Deprecated
    @PUT
    @Path("/{activityId}")
    public Response updateActivity(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId,
            @Valid ActivityRequest request) {
        
        throw new UnsupportedOperationException(
            "This endpoint is deprecated. Please use:\n" +
            "  - PUT /api/groups/" + groupId + "/activities/events/" + activityId + " (for Event)\n" +
            "  - PUT /api/groups/" + groupId + "/activities/trips/" + activityId + " (for Trip)"
        );
    }

    /**
     * Delete activity (admin only)
     * DELETE /api/groups/{groupId}/activities/{activityId}
     */
    @DELETE
    @Path("/{activityId}")
    public Response deleteActivity(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId) {
        
        Long userId = getCurrentUserId();
        activityService.deleteActivity(activityId, userId);
        
        return Response.noContent().build();
    }

    /**
     * Toggle activity completion status
     * POST /api/groups/{groupId}/activities/{activityId}/toggle-completion
     */
    @POST
    @Path("/{activityId}/toggle-completion")
    public Response toggleCompletion(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId) {
        
        Long userId = getCurrentUserId();
        ActivityDto activity = activityService.toggleActivityCompletion(activityId, userId);
        
        return Response.ok(activity).build();
    }

    /**
     * Reorder activities (drag & drop support)
     * PUT /api/groups/{groupId}/activities/reorder
     * Body: {"activityIds": [3, 1, 2, 4]}
     */
    @PUT
    @Path("/reorder")
    public Response reorderActivities(
            @PathParam("groupId") Long groupId,
            ReorderRequest request) {
        
        Long userId = getCurrentUserId();
        activityService.reorderActivities(groupId, request.activityIds, userId);
        
        return Response.noContent().build();
    }

    // =====================================================
    // PARTICIPANT OPERATIONS
    // =====================================================

    /**
     * Get all participants for an activity
     * GET /api/groups/{groupId}/activities/{activityId}/participants
     */
    @GET
    @Path("/{activityId}/participants")
    public Response getParticipants(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId) {
        
        Long userId = getCurrentUserId();
        List<ActivityParticipantDto> participants = activityService.getParticipantsByActivity(activityId, userId);
        
        return Response.ok(participants).build();
    }

    /**
     * Add participant to activity
     * POST /api/groups/{groupId}/activities/{activityId}/participants
     */
    @POST
    @Path("/{activityId}/participants")
    public Response addParticipant(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId,
            @Valid ActivityParticipantRequest request) {
        
        Long userId = getCurrentUserId();
        ActivityParticipantDto participant = activityService.addParticipant(activityId, request, userId);
        
        return Response.status(Response.Status.CREATED)
                .entity(participant)
                .build();
    }

    /**
     * Update participant status
     * PUT /api/groups/{groupId}/activities/{activityId}/participants/{participantId}
     */
    @PUT
    @Path("/{activityId}/participants/{participantId}")
    public Response updateParticipantStatus(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId,
            @PathParam("participantId") Long participantId,
            UpdateParticipantStatusRequest request) {
        
        Long userId = getCurrentUserId();
        ActivityParticipantDto participant = activityService.updateParticipantStatus(
                participantId, 
                request.status, 
                request.notes, 
                userId
        );
        
        return Response.ok(participant).build();
    }

    /**
     * Remove participant from activity
     * DELETE /api/groups/{groupId}/activities/{activityId}/participants/{participantId}
     */
    @DELETE
    @Path("/{activityId}/participants/{participantId}")
    public Response removeParticipant(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId,
            @PathParam("participantId") Long participantId) {
        
        Long userId = getCurrentUserId();
        activityService.removeParticipant(participantId, userId);
        
        return Response.noContent().build();
    }

    // =====================================================
    // EXPENSE OPERATIONS
    // =====================================================

    /**
     * Get all expenses for an activity
     * GET /api/groups/{groupId}/activities/{activityId}/expenses
     */
    @GET
    @Path("/{activityId}/expenses")
    public Response getExpenses(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId) {
        
        Long userId = getCurrentUserId();
        List<ActivityExpenseDto> expenses = activityService.getExpensesByActivity(activityId, userId);
        
        return Response.ok(expenses).build();
    }

    /**
     * Add expense to activity (with splits)
     * POST /api/groups/{groupId}/activities/{activityId}/expenses
     */
    @POST
    @Path("/{activityId}/expenses")
    public Response addExpense(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId,
            @Valid ActivityExpenseRequest request) {
        
        Long userId = getCurrentUserId();
        ActivityExpenseDto expense = activityService.addExpense(activityId, request, userId);
        
        return Response.status(Response.Status.CREATED)
                .entity(expense)
                .build();
    }

    /**
     * Delete expense
     * DELETE /api/groups/{groupId}/activities/{activityId}/expenses/{expenseId}
     */
    @DELETE
    @Path("/{activityId}/expenses/{expenseId}")
    public Response deleteExpense(
            @PathParam("groupId") Long groupId,
            @PathParam("activityId") Long activityId,
            @PathParam("expenseId") Long expenseId) {
        
        Long userId = getCurrentUserId();
        activityService.deleteExpense(expenseId, userId);
        
        return Response.noContent().build();
    }

    // =====================================================
    // HELPER CLASSES
    // =====================================================

    public static class ReorderRequest {
        public List<Long> activityIds;
    }

    public static class UpdateParticipantStatusRequest {
        public ParticipantStatus status;
        public String notes;
    }
}
