package com.payrollsystem.auth_service.service;

import com.payrollsystem.auth_service.model.User;

public interface UserService {

    // Method to update user details
    User updateUser(Long userId, User userDetails);

    // Method to enable or disable a user
    void toggleUserStatus(Long userId, boolean enabled);

    // Method to check if a user is not a super admin
    boolean isNotSuperAdmin(Long userId);
}