package com.storeapp.auth.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Exception mapper per gestire le eccezioni di validazione Bean Validation.
 * Trasforma le ConstraintViolationException in risposte HTTP 400 con dettagli degli errori.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        
        // Estrai i messaggi di errore dalle violazioni
        String errorMessages = exception.getConstraintViolations()
            .stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
        
        errorResponse.put("error", "Errore di validazione");
        errorResponse.put("message", errorMessages);
        errorResponse.put("details", exception.getConstraintViolations()
            .stream()
            .map(violation -> {
                Map<String, String> detail = new HashMap<>();
                detail.put("field", violation.getPropertyPath().toString());
                detail.put("message", violation.getMessage());
                detail.put("invalidValue", violation.getInvalidValue() != null ? 
                    violation.getInvalidValue().toString() : "null");
                return detail;
            })
            .collect(Collectors.toList()));
        
        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(errorResponse)
            .build();
    }
}
