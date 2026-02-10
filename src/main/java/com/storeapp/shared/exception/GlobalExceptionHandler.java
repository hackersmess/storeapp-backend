package com.storeapp.shared.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Exception Handler Globale per tutte le eccezioni dell'applicazione.
 *
 * Funzionalità:
 * - Gestione centralizzata di tutte le eccezioni
 * - Logging su console e file applicativo
 * - Formato errore JSON standardizzato
 * - Mappatura automatica HTTP status codes
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {

        // Log dell'eccezione
        logException(exception);

        // Gestione per tipo di eccezione
        if (exception instanceof BusinessException) {
            return handleBusinessException((BusinessException) exception);
        }

        if (exception instanceof NotFoundException) {
            return handleNotFoundException((NotFoundException) exception);
        }

        if (exception instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) exception);
        }

        if (exception instanceof WebApplicationException) {
            return handleWebApplicationException((WebApplicationException) exception);
        }

        // Eccezione generica non gestita
        return handleGenericException(exception);
    }

    /**
     * Gestisce BusinessException custom
     */
    private Response handleBusinessException(BusinessException exception) {
        LOG.warnf("Business Exception: [%s] %s", exception.getErrorCode(), exception.getMessage());

        ErrorResponse error = new ErrorResponse(
            exception.getErrorCode(),
            exception.getMessage(),
            exception.getHttpStatus(),
            getRequestPath()
        );

        return Response
            .status(exception.getHttpStatus())
            .entity(error)
            .build();
    }

    /**
     * Gestisce NotFoundException (404)
     */
    private Response handleNotFoundException(NotFoundException exception) {
        LOG.warnf("Resource Not Found: %s - Path: %s",
            exception.getMessage(), getRequestPath());

        ErrorResponse error = new ErrorResponse(
            "RESOURCE_NOT_FOUND",
            exception.getMessage() != null ? exception.getMessage() : "Resource not found",
            404,
            getRequestPath()
        );

        return Response
            .status(Response.Status.NOT_FOUND)
            .entity(error)
            .build();
    }

    /**
     * Gestisce ConstraintViolationException (Bean Validation)
     */
    private Response handleConstraintViolationException(ConstraintViolationException exception) {
        LOG.warnf("Validation Error: %s violations - Path: %s",
            exception.getConstraintViolations().size(), getRequestPath());

        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            "Validation failed",
            400,
            getRequestPath()
        );

        // Aggiungi dettagli validazioni
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            error.addValidationError(field, message);

            // Log ogni errore di validazione
            LOG.debugf("  - Field '%s': %s", field, message);
        }

        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(error)
            .build();
    }

    /**
     * Gestisce WebApplicationException generica
     */
    private Response handleWebApplicationException(WebApplicationException exception) {
        int status = exception.getResponse().getStatus();
        String message = exception.getMessage();

        LOG.warnf("Web Application Exception [%d]: %s - Path: %s",
            status, message, getRequestPath());

        ErrorResponse error = new ErrorResponse(
            "WEB_APPLICATION_ERROR",
            message != null ? message : "An error occurred",
            status,
            getRequestPath()
        );

        return Response
            .status(status)
            .entity(error)
            .build();
    }

    /**
     * Gestisce eccezioni generiche non previste (500)
     */
    private Response handleGenericException(Exception exception) {
        // Log completo con stack trace per errori non gestiti
        LOG.errorf(exception, "UNHANDLED EXCEPTION: %s - Path: %s",
            exception.getMessage(), getRequestPath());

        ErrorResponse error = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred. Please contact support.",
            500,
            getRequestPath()
        );

        return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(error)
            .build();
    }

    /**
     * Logging centralizzato delle eccezioni
     *
     * Console log: sempre
     * Application log: file application.log
     */
    private void logException(Exception exception) {
        String exceptionType = exception.getClass().getSimpleName();
        String requestPath = getRequestPath();
        LocalDateTime timestamp = LocalDateTime.now();

        // Log formattato per console
        String consoleLog = String.format(
            "\n" +
            "═══════════════════════════════════════════════════════════════\n" +
            "⚠️  EXCEPTION CAUGHT\n" +
            "═══════════════════════════════════════════════════════════════\n" +
            "  Type:      %s\n" +
            "  Message:   %s\n" +
            "  Path:      %s\n" +
            "  Timestamp: %s\n" +
            "═══════════════════════════════════════════════════════════════",
            exceptionType,
            exception.getMessage(),
            requestPath,
            timestamp
        );

        // Determina il livello di log appropriato
        if (exception instanceof BusinessException ||
            exception instanceof NotFoundException ||
            exception instanceof ConstraintViolationException) {
            // Errori gestiti: WARN level (no stack trace)
            LOG.warn(consoleLog);

            // Application log strutturato
            LOG.warnf("Exception: type=%s, message=%s, path=%s",
                exceptionType, exception.getMessage(), requestPath);

        } else {
            // Errori non gestiti: ERROR level (con stack trace)
            LOG.error(consoleLog);

            // Application log con stack trace completo
            LOG.errorf(exception, "Unhandled exception: type=%s, message=%s, path=%s",
                exceptionType, exception.getMessage(), requestPath);
        }
    }

    /**
     * Ottiene il path della richiesta HTTP
     */
    private String getRequestPath() {
        if (uriInfo != null && uriInfo.getPath() != null) {
            return "/" + uriInfo.getPath();
        }
        return "unknown";
    }
}
