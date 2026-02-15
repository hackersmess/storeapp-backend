package com.storeapp.itinerary.exception;

import com.storeapp.shared.exception.BusinessException;

/**
 * Eccezione lanciata quando si tenta di creare un itinerario per un gruppo che ne ha già uno
 */
public class ItineraryAlreadyExistsException extends BusinessException {

    public ItineraryAlreadyExistsException(Long groupId) {
        super(
            "Il gruppo con ID " + groupId + " ha già un itinerario",
            "ITINERARY_ALREADY_EXISTS",
            409
        );
    }
}
