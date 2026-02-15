package com.storeapp.itinerary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO per la creazione/aggiornamento di un itinerario
 * Le date sono ereditate dal gruppo, non servono qui
 */
public class ItineraryRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    @Size(max = 200, message = "Il nome non può superare 200 caratteri")
    public String name;

    public String description;
}
