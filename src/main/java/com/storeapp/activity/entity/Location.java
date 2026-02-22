package com.storeapp.activity.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Location embedded class (value object)
 * Used by Event (single location) and Trip (origin/destination)
 * Provider is configured globally in application.properties, not stored per location
 */
@Embeddable
public class Location {

    @Size(max = 500)
    @Column(name = "location_name", length = 500)
    public String name;

    @Size(max = 500)
    @Column(name = "location_address", length = 500)
    public String address;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    @Column(name = "location_latitude", precision = 10, scale = 7)
    public BigDecimal latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    @Column(name = "location_longitude", precision = 10, scale = 7)
    public BigDecimal longitude;

    @Size(max = 500)
    @Column(name = "location_place_id", length = 500)
    public String placeId;

    @Type(JsonBinaryType.class)
    @Column(name = "location_metadata", columnDefinition = "jsonb")
    public Map<String, Object> metadata;

    // Constructors
    public Location() {
    }

    public Location(String name, String address, BigDecimal latitude, BigDecimal longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Utility methods
    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    public String getDisplayName() {
        if (name != null && !name.isBlank()) {
            return name;
        }
        if (address != null && !address.isBlank()) {
            return address;
        }
        if (hasCoordinates()) {
            return String.format("%.4f, %.4f", latitude, longitude);
        }
        return "Unknown location";
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
