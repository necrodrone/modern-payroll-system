package com.payrollsystem.auth_service.repository;

import com.payrollsystem.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for the User entity.
 * Provides standard CRUD operations and custom query methods for user management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return An Optional containing the User if found, or empty otherwise.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user with the given username exists in the database.
     *
     * @param username The username to check.
     * @return True if a user with the username exists, false otherwise.
     */
    Boolean existsByUsername(String username);

    /**
     * Checks if a user with the given email exists in the database.
     *
     * @param email The email to check.
     * @return True if a user with the email exists, false otherwise.
     */
    Boolean existsByEmail(String email);
}