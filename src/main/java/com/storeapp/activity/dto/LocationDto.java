package com.storeapp.activity.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO for Location (embedded in Event/Trip)
 * Location provider is configured globally in application.properties
 */
public class LocationDto {

    public String name;
    public String address;
    public BigDecimal latitude;
    public BigDecimal longitude;
    public String placeId;
    public Map<String, Object> metadata;

    // Default constructor
    public LocationDto() {}

    // Full constructor
    public LocationDto(String name, String address, BigDecimal latitude, BigDecimal longitude, String placeId, Map<String, Object> metadata) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeId = placeId;
        this.metadata = metadata;
    }

    /**
     * Check if location has valid coordinates
     */
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    /**
     * Get display name (fallback to address or coordinates)
     */
    public String getDisplayName() {
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        if (address != null && !address.trim().isEmpty()) {
            return address;
        }
        if (hasCoordinates()) {
            return String.format("%.4f, %.4f", latitude, longitude);
        }
        return "No location";
    }
}
