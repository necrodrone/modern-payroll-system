package com.payrollsystem.auth_service.payload.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user login requests.
 * Contains username and password fields, with validation annotations.
 */
public class LoginRequest {
    /**
     * The username provided by the user for login. Cannot be blank.
     */
    @NotBlank
    private String username;

    /**
     * The password provided by the user for login. Cannot be blank.
     */
    @NotBlank
    private String password;

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}