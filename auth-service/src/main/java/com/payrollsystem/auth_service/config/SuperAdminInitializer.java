package com.payrollsystem.auth_service.config;

import com.payrollsystem.auth_service.model.Role;
import com.payrollsystem.auth_service.model.User;
import com.payrollsystem.auth_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SuperAdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (!userRepository.existsByUsername("superadmin")) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setEmail("superadmin@example.com");
            superAdmin.setPassword(passwordEncoder.encode("adminpassjoe1234")); // Set a strong password

            Set<Role> roles = new HashSet<>();
            roles.add(Role.ROLE_SUPER_ADMIN);
            superAdmin.setRoles(roles);

            userRepository.save(superAdmin);
        }
    }
}