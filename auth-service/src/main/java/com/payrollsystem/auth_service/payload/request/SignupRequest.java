package com.payrollsystem.auth_service.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * DTO for user signup requests.
 * Contains username, email, password, and optional roles for new user registration.
 */
public class SignupRequest {
    /**
     * The desired username for the new user. Cannot be blank and has size constraints.
     */
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    /**
     * The email address for the new user. Must be a valid email and cannot be blank.
     */
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    /**
     * The password for the new user. Cannot be blank and has size constraints.
     */
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    /**
     * Optional set of roles to assign to the new user.
     * If not provided, a default role (e.g., ROLE_USER) might be assigned by the service.
     */
    private Set<String> role;

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }
}