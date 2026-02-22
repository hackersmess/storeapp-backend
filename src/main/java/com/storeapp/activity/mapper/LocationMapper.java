package com.storeapp.activity.mapper;

import com.storeapp.activity.dto.LocationDto;
import com.storeapp.activity.entity.Location;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mapper for Location (embedded value object)
 * Converts between Location entity and LocationDto
 */
@ApplicationScoped
public class LocationMapper {

    /**
     * Convert Location entity to LocationDto
     */
    public LocationDto toDto(Location entity) {
        if (entity == null) {
            return null;
        }

        LocationDto dto = new LocationDto();
        dto.name = entity.name;
        dto.address = entity.address;
        dto.latitude = entity.latitude;
        dto.longitude = entity.longitude;
        dto.placeId = entity.placeId;
        dto.metadata = entity.metadata;
        return dto;
    }

    /**
     * Convert LocationDto to Location entity
     */
    public Location toEntity(LocationDto dto) {
        if (dto == null) {
            return new Location(); // Return empty location instead of null
        }

        Location entity = new Location();
        entity.name = dto.name;
        entity.address = dto.address;
        entity.latitude = dto.latitude;
        entity.longitude = dto.longitude;
        entity.placeId = dto.placeId;
        entity.metadata = dto.metadata;
        return entity;
    }

    /**
     * Update Location entity from DTO
     */
    public void updateEntityFromDto(LocationDto dto, Location entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.name = dto.name;
        entity.address = dto.address;
        entity.latitude = dto.latitude;
        entity.longitude = dto.longitude;
        entity.placeId = dto.placeId;
        entity.metadata = dto.metadata;
    }
}
