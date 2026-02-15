package com.storeapp.itinerary.controller;

import com.storeapp.itinerary.dto.*;
import com.storeapp.itinerary.entity.ParticipantStatus;
import com.storeapp.itinerary.service.ActivityService;
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
 * Base path: /api/itineraries/{itineraryId}/activities
 */
@Path("/api/itineraries/{itineraryId}/activities")
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

    /**
     * Create new activity
     * POST /api/itineraries/{itineraryId}/activities
     */
    @POST
    public Response createActivity(
            @PathParam("itineraryId") Long itineraryId,
            @Valid ActivityRequest request) {
        
        Long userId = getCurrentUserId();
        ActivityDto activity = activityService.createActivity(itineraryId, request, userId);
        
        return Response.status(Response.Status.CREATED)
                .entity(activity)
                .build();
    }

    /**
     * Get all activities for an itinerary
     * GET /api/itineraries/{itineraryId}/activities
     */
    @GET
    public Response getActivities(@PathParam("itineraryId") Long itineraryId) {
        Long userId = getCurrentUserId();
        List<ActivityDto> activities = activityService.getActivitiesByItinerary(itineraryId, userId);
        
        return Response.ok(activities).build();
    }

    /**
     * Get single activity (basic info)
     * GET /api/itineraries/{itineraryId}/activities/{activityId}
     */
    @GET
    @Path("/{activityId}")
    public Response getActivity(
            @PathParam("itineraryId") Long itineraryId,
            @PathParam("activityId") Long activityId) {
        
        Long userId = getCurrentUserId();
        ActivityDto activity = activityService.getActivity(activityId, userId);
        
        return Response.ok(activity).build();
    }

    /**
     * Get single activity with full details (participants + expenses)
     * GET /api/itineraries/{itineraryId}/activities/{activityId}/details
     */
    @GET
    @Path("/{activityId}/details")
    public Response getActivityWithDetails(
            @PathParam("itineraryId") Long itineraryId,
            @PathParam("activityId") Long activityId) {
        
        Long userId = getCurrentUserId();
        ActivityDto activity = activityService.getActivityWithDetails(activityId, userId);
        
        return Response.ok(activity).build();
    }

    /**
     * Update activity
     * PUT /api/itineraries/{itineraryId}/activities/{activityId}
     */
    @PUT
    @Path("/{activityId}")
    public Response updateActivity(
            @PathParam("itineraryId") Long itineraryId,
            @PathParam("activityId") Long activityId,
            @Valid ActivityRequest request) {
        
        Long userId = getCurrentUserId();
        ActivityDto activity = activityService.updateActivity(activityId, request, userId);
        
        return Response.ok(activity).build();
    }

    /**
     * Delete activity (admin only)
     * DELETE /api/itineraries/{itineraryId}/activities/{activityId}
     */
    @DELETE
    @Path("/{activityId}")
    public Response deleteActivity(
            @PathParam("itineraryId") Long itineraryId,
            @PathParam("activityId") Long activityId) {
        
        Long userId = getCurrentUserId();
        activityService.deleteActivity(activityId, userId);
        
        return Response.noContent().build();
    }

    /**
     * Toggle activity completion status
     * POST /api/itineraries/{itineraryId}/activities/{activityId}/toggle-completion
     */
    @POST
    @Path("/{activityId}/toggle-completion")
    public Response toggleCompletion(
            @PathParam("itineraryId") Long itineraryId,
            @PathParam("activityId") Long activityId) {
        
        Long userId = getCurrentUserId();
        ActivityDto activity = activityService.toggleActivityCompletion(activityId, userId);
        
        return Response.ok(activity).build();
    }

    /**
     * Reorder activities (drag & drop support)
     * PUT /api/itineraries/{itineraryId}/activities/reorder
     * Body: {"activityIds": [3, 1, 2, 4]}
     */
    @PUT
    @Path("/reorder")
    public Response reorderActivities(
            @PathParam("itineraryId") Long itineraryId,
            ReorderRequest request) {
        
        Long userId = getCurrentUserId();
        activityService.reorderActivities(itineraryId, request.activityIds, userId);
        
        return Response.noContent().build();
    }

    // =====================================================
    // PARTICIPANT OPERATIONS
    // =====================================================

    /**
     * Get all participants for an activity
     * GET /api/itineraries/{itineraryId}/activities/{activityId}/participants
     */
    @GET
    @Path("/{activityId}/participants")
    public Response getParticipants(
            @PathParam("itineraryId") Long itineraryId,
            @PathParam("activityId") Long activityId) {
        
        Long userId = getCurrentUserId();
        List<ActivityParticipantDto> participants = activityService.getParticipantsByActivity(activityId, userId);
        
        return Response.ok(participants).build();
    }

    /**
     * Add participant to activity
     * POST /api/itineraries/{itineraryId}/activities/{activityId}/participants
     */
    @POST
    @Path("/{activityId}/participants")
    public Response addParticipant(
            @PathParam("itineraryId") Long itineraryId,
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
     * PUT /api/itineraries/{itineraryId}/activities/{activityId}/participants/{participantId}
     */
    @PUT
    @Path("/{activityId}/participants/{participantId}")
    public Response updateParticipantStatus(
            @PathParam("itineraryId") Long itineraryId,
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
     * DELETE /api/itineraries/{itineraryId}/activities/{activityId}/participants/{participantId}
     */
    @DELETE
    @Path("/{activityId}/participants/{participantId}")
    public Response removeParticipant(
            @PathParam("itineraryId") Long itineraryId,
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
     * GET /api/itineraries/{itineraryId}/activities/{activityId}/expenses
     */
    @GET
    @Path("/{activityId}/expenses")
    public Response getExpenses(
            @PathParam("itineraryId") Long itineraryId,
            @PathParam("activityId") Long activityId) {
        
        Long userId = getCurrentUserId();
        List<ActivityExpenseDto> expenses = activityService.getExpensesByActivity(activityId, userId);
        
        return Response.ok(expenses).build();
    }

    /**
     * Add expense to activity (with splits)
     * POST /api/itineraries/{itineraryId}/activities/{activityId}/expenses
     */
    @POST
    @Path("/{activityId}/expenses")
    public Response addExpense(
            @PathParam("itineraryId") Long itineraryId,
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
     * DELETE /api/itineraries/{itineraryId}/activities/{activityId}/expenses/{expenseId}
     */
    @DELETE
    @Path("/{activityId}/expenses/{expenseId}")
    public Response deleteExpense(
            @PathParam("itineraryId") Long itineraryId,
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
