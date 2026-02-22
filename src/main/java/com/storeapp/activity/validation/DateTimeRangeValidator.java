package com.storeapp.activity.validation;

import com.storeapp.activity.dto.EventRequest;
import com.storeapp.activity.dto.TripRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

/**
 * Validator for ValidDateTimeRange annotation
 */
public class DateTimeRangeValidator implements ConstraintValidator<ValidDateTimeRange, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null checks
        }

        if (value instanceof EventRequest event) {
            return validateEventRequest(event, context);
        } else if (value instanceof TripRequest trip) {
            return validateTripRequest(trip, context);
        }

        return true;
    }

    private boolean validateEventRequest(EventRequest request, ConstraintValidatorContext context) {
        // All fields must be non-null
        if (request.startDate == null || request.endDate == null || 
            request.startTime == null || request.endTime == null) {
            return false; // Let @NotNull handle this
        }

        LocalDateTime start = LocalDateTime.of(request.startDate, request.startTime);
        LocalDateTime end = LocalDateTime.of(request.endDate, request.endTime);

        if (!end.isAfter(start)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Data e ora di fine devono essere successive a quelle di inizio"
            ).addPropertyNode("endDate").addConstraintViolation();
            return false;
        }

        return true;
    }

    private boolean validateTripRequest(TripRequest request, ConstraintValidatorContext context) {
        // All fields must be non-null
        if (request.startDate == null || request.endDate == null || 
            request.departureTime == null || request.arrivalTime == null) {
            return false; // Let @NotNull handle this
        }

        LocalDateTime start = LocalDateTime.of(request.startDate, request.departureTime);
        LocalDateTime end = LocalDateTime.of(request.endDate, request.arrivalTime);

        if (!end.isAfter(start)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Data e ora di arrivo devono essere successive a quelle di partenza"
            ).addPropertyNode("endDate").addConstraintViolation();
            return false;
        }

        return true;
    }
}
