package com.storeapp.itinerary.exception;

import com.storeapp.shared.exception.BusinessException;

/**
 * Eccezione lanciata quando un'attività non viene trovata
 */
public class ActivityNotFoundException extends BusinessException {

    public ActivityNotFoundException(Long activityId) {
        super(
            "Attività con ID " + activityId + " non trovata",
            "ACTIVITY_NOT_FOUND",
            404
        );
    }
}
