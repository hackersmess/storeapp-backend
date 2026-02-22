package com.storeapp.activity.dto;

import com.storeapp.activity.entity.TransportMode;
import com.storeapp.activity.validation.ValidDateTimeRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Request DTO for creating/updating Trip activities
 * Trips are travel activities with origin and destination (flights, trains, car trips, etc.)
 */
@ValidDateTimeRange
public class TripRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 255, message = "Il nome non può superare 255 caratteri")
    public String name;

    public String description;

    @NotNull(message = "La data di inizio è obbligatoria")
    public LocalDate startDate;

    @NotNull(message = "La data di fine è obbligatoria")
    public LocalDate endDate;

    public LocalTime startTime;

    public LocalTime endTime;

    // Origin location (embedded)
    @Size(max = 500, message = "Il nome dell'origine non può superare 500 caratteri")
    public String originName;

    @Size(max = 500, message = "L'indirizzo dell'origine non può superare 500 caratteri")
    public String originAddress;

    public BigDecimal originLatitude;

    public BigDecimal originLongitude;

    @Size(max = 500, message = "Il place ID dell'origine non può superare 500 caratteri")
    public String originPlaceId;

    public Map<String, Object> originMetadata;

    // Destination location (embedded)
    @Size(max = 500, message = "Il nome della destinazione non può superare 500 caratteri")
    public String destinationName;

    @Size(max = 500, message = "L'indirizzo della destinazione non può superare 500 caratteri")
    public String destinationAddress;

    public BigDecimal destinationLatitude;

    public BigDecimal destinationLongitude;

    @Size(max = 500, message = "Il place ID della destinazione non può superare 500 caratteri")
    public String destinationPlaceId;

    public Map<String, Object> destinationMetadata;

    // Trip-specific fields
    @NotNull(message = "Il mezzo di trasporto è obbligatorio")
    public TransportMode transportMode = TransportMode.OTHER;

    @NotNull(message = "L'ora di partenza è obbligatoria")
    public LocalTime departureTime;

    @NotNull(message = "L'ora di arrivo è obbligatoria")
    public LocalTime arrivalTime;

    @Size(max = 255, message = "Il riferimento prenotazione non può superare 255 caratteri")
    public String bookingReference;

    // Common fields
    public Boolean isCompleted = false;

    public Integer displayOrder = 0;

    public BigDecimal totalCost;

    // Participants - IDs of GroupMembers participating in this activity (at least one required)
    @NotEmpty(message = "Almeno un partecipante è obbligatorio")
    public List<Long> participantIds;

    // Semantic aliases for Trip (departure/arrival instead of start/end)
    // These map to the base startDate/endDate fields for better UX
    
    /**
     * Get departure date (alias for startDate)
     */
    public LocalDate getDepartureDate() {
        return this.startDate;
    }

    /**
     * Set departure date (alias for startDate)
     */
    public void setDepartureDate(LocalDate departureDate) {
        this.startDate = departureDate;
    }

    /**
     * Get arrival date (alias for endDate)
     */
    public LocalDate getArrivalDate() {
        return this.endDate;
    }

    /**
     * Set arrival date (alias for endDate)
     */
    public void setArrivalDate(LocalDate arrivalDate) {
        this.endDate = arrivalDate;
    }
}
