package com.payrollsystem.auth_service.payload.response;

/**
 * Generic DTO for simple message responses.
 * Used for acknowledging requests or returning error messages.
 */
public class MessageResponse {
    /**
     * The message to be conveyed (e.g., success message, error message).
     */
    private String message;

    /**
     * Constructor for a message response.
     *
     * @param message The message string.
     */
    public MessageResponse(String message) {
        this.message = message;
    }

    // Getter and Setter

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}