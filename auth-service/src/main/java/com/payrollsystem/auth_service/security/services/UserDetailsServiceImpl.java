package com.payrollsystem.auth_service.security.services;

import com.payrollsystem.auth_service.model.User;
import com.payrollsystem.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom implementation of Spring Security's {@link UserDetailsService}.
 * This service is responsible for loading user-specific data during the authentication process.
 * It retrieves user information from the database and maps it to a {@link UserDetails} object.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     * Loads user details by username.
     * This method is called by Spring Security during the authentication process.
     * It queries the database for a user with the given username and, if found,
     * builds a {@link UserDetailsImpl} object from the user data.
     *
     * @param username The username identifying the user whose data is required.
     * @return A fully populated user record (an instance of {@link UserDetailsImpl})
     * @throws UsernameNotFoundException if the user could not be found or has no granted authorities.
     */
    @Override
    @Transactional // Ensures the entire operation runs within a single transaction
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user by username in the repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Build and return a UserDetailsImpl object from the retrieved User entity
        return UserDetailsImpl.build(user);
    }
}