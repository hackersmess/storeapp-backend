package com.storeapp.itinerary.exception;

import com.storeapp.shared.exception.BusinessException;

/**
 * Eccezione lanciata quando un itinerario non viene trovato
 */
public class ItineraryNotFoundException extends BusinessException {

    public ItineraryNotFoundException(Long itineraryId) {
        super(
            "Itinerario con ID " + itineraryId + " non trovato",
            "ITINERARY_NOT_FOUND",
            404
        );
    }

    public ItineraryNotFoundException(String message) {
        super(message, "ITINERARY_NOT_FOUND", 404);
    }
}
