package com.payrollsystem.auth_service.service;

import com.payrollsystem.auth_service.model.User;
import com.payrollsystem.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User updateUser(Long userId, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // Update user details
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            // You can also add logic to update other fields like roles if needed
            return userRepository.save(user); // Save the updated user
        } else {
            throw new RuntimeException("User not found with id " + userId);
        }
    }

    @Override
    public void toggleUserStatus(Long userId, boolean enabled) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEnabled(enabled); // Set the enabled status
            userRepository.save(user); // Save the updated user
        } else {
            throw new RuntimeException("User not found with id " + userId);
        }
    }

    @Override
    public boolean isNotSuperAdmin(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user.getRoles().stream()
                    .noneMatch(role -> role.name().equals("ROLE_SUPER_ADMIN"));
        }
        return false;
    }
}