package com.payrollsystem.auth_service.controller;

import com.payrollsystem.auth_service.payload.request.LoginRequest;
import com.payrollsystem.auth_service.payload.request.SignupRequest;
import com.payrollsystem.auth_service.payload.response.JwtResponse;
import com.payrollsystem.auth_service.payload.response.MessageResponse;
import com.payrollsystem.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication and authorization operations.
 * Provides endpoints for user registration (signup) and login (signin).
 * Also includes optional test endpoints for demonstrating role-based access.
 */
@CrossOrigin(origins = "*", maxAge = 3600) // Allow cross-origin requests for development
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and login APIs")
public class AuthController {

    @Autowired
    AuthService authService;

    /**
     * Endpoint for user registration.
     *
     * @param signUpRequest The DTO containing user registration details.
     * @return ResponseEntity with a success or error message.
     */
    @PostMapping("/signup")
    @Operation(summary = "Register a new user",
            description = "Creates a new user account with provided username, email, password, and optional roles.")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        MessageResponse response = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for user login.
     * Authenticates the user and returns a JWT token upon successful login.
     *
     * @param loginRequest The DTO containing user login credentials.
     * @return ResponseEntity with JwtResponse containing token and user info.
     */
    @PostMapping("/signin")
    @Operation(summary = "Authenticate user and get JWT token",
            description = "Authenticates user credentials and returns a JWT token for authorized access.")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    // Optional Test Endpoints for demonstrating authenticated access with roles

    @GetMapping("/test/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Test user access",
            description = "Accessible by users with ROLE_USER or ROLE_ADMIN.")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/test/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Test admin access",
            description = "Accessible only by users with ROLE_ADMIN.")
    public String adminAccess() {
        return "Admin Board.";
    }
}