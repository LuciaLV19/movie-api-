package movies.controller;

import movies.exception.BadRequestException;
import movies.exception.ImportMovieException;
import movies.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for the REST API.
 * Converts all application exceptions into standardized HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles requests to non-existing endpoints (404).
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFound(NoHandlerFoundException ex) {

        return buildResponse(HttpStatus.NOT_FOUND, "Route not found","The requested endpoint does not exist.");
    }

    /**
     * Handles custom "resource not found" exceptions.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {

        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage());
    }

    /**
     * Handles bad requests (invalid input or business rules violations).
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex) {

        return buildResponse(HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage());
    }

    /**
     * Handles errors related to external services (e.g. OMDb API).
     */
    @ExceptionHandler(ImportMovieException.class)
    public ResponseEntity<Object> handleImportMovieException(ImportMovieException ex) {

        return buildResponse(HttpStatus.BAD_GATEWAY, "External service error", ex.getMessage());
    }

    /**
     * Handles incorrect URL parameter types (e.g. /movies/abc instead of /movies/1).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String message = String.format(
                "Parameter '%s' must be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        return buildResponse(HttpStatus.BAD_REQUEST,"Invalid URL parameter", message);
    }

    /**
     * Fallback handler for all unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error",ex.getMessage());
    }

    /**
     * Builds a standardized API error response.
     */
    private ResponseEntity<Object> buildResponse(HttpStatus status, String error, String message) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);

        return new ResponseEntity<>(body, status);
    }
}