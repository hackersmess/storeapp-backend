package com.storeapp.activity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

/**
 * Event - Activity at a single location
 * 
 * Examples:
 * - Restaurant visit
 * - Museum tour
 * - Beach day
 * - Hotel stay (multi-day: start_date to end_date)
 * - Park picnic
 * - Shopping
 * 
 * Uses embedded Location for geographic data
 * Location provider configured globally in application.properties
 */
@Entity
@DiscriminatorValue("EVENT")
public class Event extends Activity {

    // Embedded location with event_ prefix for SINGLE_TABLE
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "event_location_name")),
        @AttributeOverride(name = "address", column = @Column(name = "event_location_address")),
        @AttributeOverride(name = "latitude", column = @Column(name = "event_location_latitude")),
        @AttributeOverride(name = "longitude", column = @Column(name = "event_location_longitude")),
        @AttributeOverride(name = "placeId", column = @Column(name = "event_location_place_id")),
        @AttributeOverride(name = "metadata", column = @Column(name = "event_location_metadata"))
    })
    public Location location;

    // Event-specific fields with event_ prefix for SINGLE_TABLE
    @Enumerated(EnumType.STRING)
    @Column(name = "event_category", nullable = false, length = 50)
    public EventCategory category = EventCategory.OTHER;

    @Size(max = 1000)
    @Column(name = "event_booking_url", length = 1000)
    public String bookingUrl;

    @Size(max = 255)
    @Column(name = "event_booking_reference", length = 255)
    public String bookingReference;

    @Column(name = "event_reservation_time")
    public LocalTime reservationTime;

    // Constructors
    public Event() {
        this.location = new Location();
    }

    public Event(String name, Location location, EventCategory category) {
        this.name = name;
        this.location = location != null ? location : new Location();
        this.category = category != null ? category : EventCategory.OTHER;
    }

    @Override
    public String getActivityType() {
        return "EVENT";
    }

    /**
     * Get display location (fallback to location name or coordinates)
     */
    public String getDisplayLocation() {
        if (location != null) {
            return location.getDisplayName();
        }
        return "No location";
    }

    /**
     * Check if event has valid coordinates
     */
    public boolean hasValidLocation() {
        return location != null && location.hasCoordinates();
    }

    @Override
    public String toString() {
        return String.format("Event{id=%d, name='%s', category=%s, location='%s'}", 
            id, name, category, getDisplayLocation());
    }
}
