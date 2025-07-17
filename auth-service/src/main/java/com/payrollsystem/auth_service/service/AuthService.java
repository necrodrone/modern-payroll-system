package com.payrollsystem.auth_service.service;

import com.payrollsystem.auth_service.model.Role;
import com.payrollsystem.auth_service.model.User;
import com.payrollsystem.auth_service.payload.request.LoginRequest;
import com.payrollsystem.auth_service.payload.request.SignupRequest;
import com.payrollsystem.auth_service.payload.response.JwtResponse;
import com.payrollsystem.auth_service.payload.response.MessageResponse;
import com.payrollsystem.auth_service.repository.UserRepository;
import com.payrollsystem.auth_service.security.jwt.JwtUtils;
import com.payrollsystem.auth_service.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class handling core authentication and user management logic.
 * This includes user registration, login, and potentially role assignment.
 */
@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * Registers a new user.
     * Checks for unique username and email, encodes the password, and assigns roles.
     *
     * @param signUpRequest The signup request DTO containing user details.
     * @return A MessageResponse indicating success or failure of registration.
     */
    @Transactional
    public MessageResponse registerUser(SignupRequest signUpRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword())); // Encode password

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // If no roles are specified, assign default ROLE_USER
            roles.add(Role.ROLE_USER);
        } else {
            // Assign roles based on the request
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        roles.add(Role.ROLE_ADMIN);
                        break;
                    case "user":
                        roles.add(Role.ROLE_USER);
                        break;
                    default:
                        // Handle unknown roles or assign default
                        roles.add(Role.ROLE_USER); // Default to user if role is unknown
                        break;
                }
            });
        }

        user.setRoles(roles); // Set roles for the new user
        userRepository.save(user); // Save the user to the database

        return new MessageResponse("User registered successfully!");
    }

    /**
     * Authenticates a user and generates a JWT token upon successful login.
     *
     * @param loginRequest The login request DTO containing username and password.
     * @return A JwtResponse containing the JWT token and user details.
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // Authenticate the user using Spring Security's AuthenticationManager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Set the authenticated user in the SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Get UserDetailsImpl from the authentication object
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Extract roles as a list of strings
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Return JWT response with token and user details
        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }
}