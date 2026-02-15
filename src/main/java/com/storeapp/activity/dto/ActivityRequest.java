package com.storeapp.activity.dto;

import com.storeapp.activity.entity.LocationProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

/**
 * DTO per la creazione/aggiornamento di un'attività
 */
public class ActivityRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 200, message = "Il nome non può superare 200 caratteri")
    public String name;

    public String description;

    public LocalDate scheduledDate;

    public LocalTime startTime;

    public LocalTime endTime;

    // Location fields
    @Size(max = 200, message = "Il nome della location non può superare 200 caratteri")
    public String locationName;

    @Size(max = 500, message = "L'indirizzo non può superare 500 caratteri")
    public String locationAddress;

    public BigDecimal locationLat;

    public BigDecimal locationLng;

    @Size(max = 500, message = "Il place ID non può superare 500 caratteri")
    public String locationPlaceId;

    public LocationProvider locationProvider = LocationProvider.MAPBOX;

    public Map<String, Object> locationMetadata;

    public Boolean isCompleted = false;

    public Integer displayOrder = 0;
}
