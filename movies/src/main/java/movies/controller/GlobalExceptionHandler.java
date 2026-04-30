package movies.controller;

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
 * Intercepts exceptions thrown by controllers and converts them into consistent HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles requests to non-existing routes (404).
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFound(NoHandlerFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Route not found", "The requested resource does not exist.");
    }

    /**
     * Handles unexpected exceptions globally.
     * Also filters known Swagger-related errors to return 404 instead of 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllErrors(Exception ex) {

        String message = ex.getMessage() != null ? ex.getMessage() : "";

        // Allow Swagger-related requests to be treated as NOT FOUND instead of server errors
        if (message.contains("swagger")
                || message.contains("api-docs")
                || message.contains("webjars")
                || message.contains("No static resource")) {

            return buildResponse(HttpStatus.NOT_FOUND, "Resource not found", message);
        }

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", message);
    }

    /**
     * Handles null pointer exceptions, typically caused by missing resources.
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(NullPointerException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource not found", "The requested resource does not exist.");
    }

    /**
     * Handles invalid URL parameter types (e.g. expected number but received string).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String message = String.format(
                "Parameter '%s' must be of type %s",
                ex.getName(),
                ex.getRequiredType().getSimpleName()
        );

        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid URL format", message);
    }

    /**
     * Handles invalid business logic inputs (e.g. negative values, invalid states).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgs(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid data", ex.getMessage());
    }

    /**
     * Builds a standardized API error response.
     * Ensures all errors follow a consistent structure across the application.
     */
    private ResponseEntity<Object> buildResponse(HttpStatus status, String error, String message) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("error_count", 1);

        return new ResponseEntity<>(body, status);
    }
}