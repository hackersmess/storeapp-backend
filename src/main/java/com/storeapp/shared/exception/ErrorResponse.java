package com.storeapp.shared.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO standard per le risposte di errore dell'API.
 * Formato consistente per tutti gli errori HTTP.
 */
public class ErrorResponse {

    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private int status;
    private List<ValidationError> validationErrors;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
        this.validationErrors = new ArrayList<>();
    }

    public ErrorResponse(String errorCode, String message, int status) {
        this();
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
    }

    public ErrorResponse(String errorCode, String message, int status, String path) {
        this(errorCode, message, status);
        this.path = path;
    }

    /**
     * Aggiunge un errore di validazione
     */
    public void addValidationError(String field, String error) {
        validationErrors.add(new ValidationError(field, error));
    }

    // Getters & Setters

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    /**
     * Classe interna per errori di validazione
     */
    public static class ValidationError {
        private String field;
        private String message;

        public ValidationError() {}

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
