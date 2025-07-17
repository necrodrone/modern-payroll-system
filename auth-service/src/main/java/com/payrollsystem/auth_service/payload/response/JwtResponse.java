package com.payrollsystem.auth_service.payload.response;

import java.util.List;

/**
 * DTO for JWT authentication responses.
 * Sent back to the client upon successful login, containing the JWT token
 * and basic user information.
 */
public class JwtResponse {
    /**
     * The generated JWT access token.
     */
    private String token;
    /**
     * Type of token, typically "Bearer".
     */
    private String type = "Bearer";
    /**
     * User's ID.
     */
    private Long id;
    /**
     * User's username.
     */
    private String username;
    /**
     * User's email.
     */
    private String email;
    /**
     * List of roles assigned to the user.
     */
    private List<String> roles;

    /**
     * Constructor for a successful JWT response.
     *
     * @param accessToken The JWT token generated upon successful authentication.
     * @param id The ID of the authenticated user.
     * @param username The username of the authenticated user.
     * @param email The email of the authenticated user.
     * @param roles A list of roles associated with the authenticated user.
     */
    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    // Getters and Setters

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}