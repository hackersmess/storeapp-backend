package com.storeapp.activity.mapper;

import com.storeapp.activity.dto.EventDto;
import com.storeapp.activity.dto.EventRequest;
import com.storeapp.activity.entity.Event;
import com.storeapp.activity.entity.Location;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Mapper for Event entity and DTOs
 * Converts between Event entity and EventDto/EventRequest
 */
@ApplicationScoped
public class EventMapper {

    @Inject
    LocationMapper locationMapper;

    /**
     * Convert Event entity to EventDto
     */
    public EventDto toDto(Event entity) {
        if (entity == null) {
            return null;
        }

        EventDto dto = new EventDto();
        dto.id = entity.id;
        dto.groupId = entity.group != null ? entity.group.id : null;
        dto.name = entity.name;
        dto.description = entity.description;
        dto.startDate = entity.startDate;
        dto.endDate = entity.endDate;
        dto.startTime = entity.startTime;
        dto.endTime = entity.endTime;
        dto.activityType = "EVENT";

        // Location
        dto.location = locationMapper.toDto(entity.location);

        // Event-specific fields
        dto.category = entity.category;
        dto.bookingUrl = entity.bookingUrl;
        dto.bookingReference = entity.bookingReference;
        dto.reservationTime = entity.reservationTime;

        // Common fields
        dto.isCompleted = entity.isCompleted;
        dto.displayOrder = entity.displayOrder;
        dto.totalCost = entity.totalCost;
        dto.createdAt = entity.createdAt;
        dto.updatedAt = entity.updatedAt;
        dto.createdBy = entity.createdBy != null ? entity.createdBy.getId() : null;

        // Participant statistics
        dto.confirmedCount = entity.getConfirmedCount();

        return dto;
    }

    /**
     * Convert EventRequest to Event entity
     */
    public Event toEntity(EventRequest request) {
        if (request == null) {
            return null;
        }

        Event entity = new Event();
        updateEntityFromRequest(request, entity);
        return entity;
    }

    /**
     * Update Event entity from EventRequest
     */
    public void updateEntityFromRequest(EventRequest request, Event entity) {
        if (request == null || entity == null) {
            return;
        }

        entity.name = request.name;
        entity.description = request.description;
        entity.startDate = request.startDate;
        entity.endDate = request.endDate;
        entity.startTime = request.startTime;
        entity.endTime = request.endTime;

        // Location
        if (entity.location == null) {
            entity.location = new Location();
        }
        entity.location.name = request.locationName;
        entity.location.address = request.locationAddress;
        entity.location.latitude = request.locationLatitude;
        entity.location.longitude = request.locationLongitude;
        entity.location.placeId = request.locationPlaceId;
        entity.location.metadata = request.locationMetadata;

        // Event-specific fields
        entity.category = request.category;
        entity.bookingUrl = request.bookingUrl;
        entity.bookingReference = request.bookingReference;
        entity.reservationTime = request.reservationTime;

        // Common fields
        entity.isCompleted = request.isCompleted;
        entity.displayOrder = request.displayOrder;
        entity.totalCost = request.totalCost != null ? request.totalCost : java.math.BigDecimal.ZERO;
    }
}
