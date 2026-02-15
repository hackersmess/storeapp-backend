package com.storeapp.itinerary.controller;

import com.storeapp.itinerary.dto.ItineraryDto;
import com.storeapp.itinerary.dto.ItineraryRequest;
import com.storeapp.itinerary.service.ItineraryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * REST Controller for Itinerary operations
 * Base path: /api/groups/{groupId}/itinerary
 */
@Path("/api/groups/{groupId}/itinerary")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class ItineraryController {

    @Inject
    ItineraryService itineraryService;

    @Inject
    JsonWebToken jwt;

    private Long getCurrentUserId() {
        return Long.parseLong(jwt.getSubject());
    }

    /**
     * Create itinerary for a group
     * POST /api/groups/{groupId}/itinerary
     */
    @POST
    public Response createItinerary(
            @PathParam("groupId") Long groupId,
            @Valid ItineraryRequest request) {
        
        Long userId = getCurrentUserId();
        ItineraryDto itinerary = itineraryService.createItinerary(groupId, request, userId);
        
        return Response.status(Response.Status.CREATED)
                .entity(itinerary)
                .build();
    }

    /**
     * Get itinerary by group ID (basic info)
     * GET /api/groups/{groupId}/itinerary
     */
    @GET
    public Response getItinerary(@PathParam("groupId") Long groupId) {
        Long userId = getCurrentUserId();
        ItineraryDto itinerary = itineraryService.getItinerary(groupId, userId);
        
        return Response.ok(itinerary).build();
    }

    /**
     * Get specific itinerary by ID (basic info)
     * GET /api/groups/{groupId}/itinerary/{id}
     */
    @GET
    @Path("/{id}")
    public Response getItineraryById(
            @PathParam("groupId") Long groupId,
            @PathParam("id") Long itineraryId) {
        Long userId = getCurrentUserId();
        ItineraryDto itinerary = itineraryService.getItineraryById(groupId, itineraryId, userId);
        
        return Response.ok(itinerary).build();
    }

    /**
     * Get itinerary with all activities
     * GET /api/groups/{groupId}/itinerary/full
     */
    @GET
    @Path("/full")
    public Response getItineraryWithActivities(@PathParam("groupId") Long groupId) {
        Long userId = getCurrentUserId();
        ItineraryDto itinerary = itineraryService.getItineraryWithActivities(groupId, userId);
        
        return Response.ok(itinerary).build();
    }

    /**
     * Get specific itinerary with all activities
     * GET /api/groups/{groupId}/itinerary/{id}/full
     */
    @GET
    @Path("/{id}/full")
    public Response getItineraryByIdWithActivities(
            @PathParam("groupId") Long groupId,
            @PathParam("id") Long itineraryId) {
        Long userId = getCurrentUserId();
        ItineraryDto itinerary = itineraryService.getItineraryByIdWithActivities(groupId, itineraryId, userId);
        
        return Response.ok(itinerary).build();
    }

    /**
     * Update itinerary
     * PUT /api/groups/{groupId}/itinerary
     */
    @PUT
    public Response updateItinerary(
            @PathParam("groupId") Long groupId,
            @Valid ItineraryRequest request) {
        
        Long userId = getCurrentUserId();
        ItineraryDto itinerary = itineraryService.updateItinerary(groupId, request, userId);
        
        return Response.ok(itinerary).build();
    }

    /**
     * Update specific itinerary by ID
     * PUT /api/groups/{groupId}/itinerary/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateItineraryById(
            @PathParam("groupId") Long groupId,
            @PathParam("id") Long itineraryId,
            @Valid ItineraryRequest request) {
        
        Long userId = getCurrentUserId();
        ItineraryDto itinerary = itineraryService.updateItineraryById(groupId, itineraryId, request, userId);
        
        return Response.ok(itinerary).build();
    }

    /**
     * Delete itinerary (admin only)
     * DELETE /api/groups/{groupId}/itinerary
     */
    @DELETE
    public Response deleteItinerary(@PathParam("groupId") Long groupId) {
        Long userId = getCurrentUserId();
        itineraryService.deleteItinerary(groupId, userId);
        
        return Response.noContent().build();
    }

    /**
     * Delete specific itinerary by ID (admin only)
     * DELETE /api/groups/{groupId}/itinerary/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteItineraryById(
            @PathParam("groupId") Long groupId,
            @PathParam("id") Long itineraryId) {
        Long userId = getCurrentUserId();
        itineraryService.deleteItineraryById(groupId, itineraryId, userId);
        
        return Response.noContent().build();
    }
}
