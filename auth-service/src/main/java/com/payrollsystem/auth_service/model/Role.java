package com.payrollsystem.auth_service.model;

/**
 * Enum representing different roles a user can have in the application.
 * This approach is simpler if roles are fixed and known beforehand.
 * If roles need to be dynamically managed, this can be converted into a JPA Entity.
 */
public enum Role {
    /**
     * Standard user role with basic permissions.
     */
    ROLE_USER,
    /**
     * Administrator role with elevated permissions.
     */
    ROLE_ADMIN
}