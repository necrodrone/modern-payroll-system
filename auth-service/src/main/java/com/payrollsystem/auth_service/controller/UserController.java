package com.payrollsystem.auth_service.controller;

import com.payrollsystem.auth_service.model.User;
import com.payrollsystem.auth_service.repository.UserRepository;
import com.payrollsystem.auth_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for managing user accounts")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Get all users",
            description = "Retrieves a list of all users. Accessible by ADMIN and SUPER_ADMIN roles.")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('ADMIN') and @userServiceImpl.isNotSuperAdmin(#userId))")
    @Operation(summary = "Update user details",
            description = "Updates a user's details. SUPER_ADMIN can update any user. ADMIN can update any non-SUPER_ADMIN user.")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(userId, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('ADMIN') and @userServiceImpl.isNotSuperAdmin(#userId))")
    @Operation(summary = "Enable or disable a user",
            description = "Toggles the enabled status of a user. SUPER_ADMIN can modify any user. ADMIN can modify any non-SUPER_ADMIN user.")
    public ResponseEntity<Void> toggleUserStatus(@PathVariable Long userId, @RequestParam boolean enabled) {
        userService.toggleUserStatus(userId, enabled);
        return ResponseEntity.ok().build();
    }
}