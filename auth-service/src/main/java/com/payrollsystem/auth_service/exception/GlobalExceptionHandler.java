package com.payrollsystem.auth_service.exception;

import com.payrollsystem.auth_service.payload.response.MessageResponse; // Potentially remove this if using unified Map response
import jakarta.servlet.http.HttpServletRequest; // Import HttpServletRequest
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest; // Keep WebRequest for compatibility if needed, but HttpServletRequest is often better for paths.

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global exception handler for the authentication service.
 * This class uses {@code @ControllerAdvice} to provide centralized exception handling
 * across all `@RequestMapping` methods. It catches specific exceptions and returns
 * appropriate HTTP responses with a consistent structure.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Helper method to build a consistent error response structure.
     *
     * @param status The HTTP status code.
     * @param message The error message or a map of validation errors.
     * @param request The HttpServletRequest to get the request URI.
     * @return A Map representing the standardized error response.
     */
    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, Object message, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase()); // e.g., "Not Found", "Bad Request"
        body.put("message", message); // This can be a String or a Map of validation errors
        body.put("path", request.getRequestURI());
        return new ResponseEntity<>(body, status);
    }

    /**
     * Handles {@link UsernameNotFoundException}.
     * This exception is typically thrown when a user with the specified username
     * is not found in the database during authentication.
     *
     * @param ex The UsernameNotFoundException instance.
     * @param request The current HttpServletRequest.
     * @return A ResponseEntity with a 404 Not Found status and an error message.
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /**
     * Handles {@link BadCredentialsException}.
     * This exception is thrown when the authentication credentials (e.g., password)
     * are incorrect.
     *
     * @param ex The BadCredentialsException instance.
     * @param request The current HttpServletRequest.
     * @return A ResponseEntity with a 401 Unauthorized status and an error message.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid Username or Password", request);
    }

    /**
     * Handles {@link MethodArgumentNotValidException}.
     * This exception is thrown when method arguments annotated with {@code @Valid} fail validation.
     * It collects all validation errors and returns them in a map within the standardized response.
     *
     * @param ex The MethodArgumentNotValidException instance.
     * @param request The current HttpServletRequest.
     * @return A ResponseEntity with a 400 Bad Request status and a map of validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validationErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage()));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, validationErrors, request);
    }

    /**
     * Handles all other unhandled exceptions.
     * This is a fallback handler for any other exceptions that are not specifically caught.
     *
     * @param ex The Exception instance.
     * @param request The current HttpServletRequest.
     * @return A ResponseEntity with a 500 Internal Server Error status and a generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, HttpServletRequest request) {
        // Log the exception for debugging purposes in production, consider more structured logging
        ex.printStackTrace(); // Keep this for dev, but in prod use a proper logging framework
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage(), request);
    }
}