package com.se310.store.service;

import com.se310.store.model.User;
import com.se310.store.model.UserRole;
import com.se310.store.repository.UserRepository;
import com.se310.store.security.PasswordEncryption;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

/**
 * This class is responsible for authenticating users and managing user data.
 * Handles password encryption/decryption for secure password storage.
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 * @since 2025-09-25
 **/
public class AuthenticationService {

    //TODO: Implement authentication service for User operations
    //TODO: Implement authorizations service for Store operations
    //TODO: Implement management of User related data in the persistent storage
    //TODO: Implement Service Layer Pattern

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user using HTTP Basic Authentication.
     *
     * @param authHeader The Authorization header value (e.g., "Basic base64(email:password)")
     * @return Optional containing the authenticated User, or empty if authentication fails
     */
    public Optional<User> authenticateBasic(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return Optional.empty();
        }

        try {
            //TODO: Implement User Authentication logic
            String base64Credentials = authHeader.substring("Basic ".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

            // Split credentials into email and password
            String[] parts = credentials.split(":", 2);
            if (parts.length != 2) {
                return Optional.empty();
            }

            String email = parts[0];
            String password = parts[1];

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return Optional.empty();
            }
            User user = userOpt.get();
            String storedPassword = user.getPassword();
            if (storedPassword == null) {
                return Optional.empty();
            }

            boolean matches;
            if (PasswordEncryption.isEncrypted(storedPassword)) {
                matches = PasswordEncryption.verify(password, storedPassword);
            } else {
                matches = storedPassword.equals(password);
            }

            return matches ? Optional.of(user) : Optional.empty();

        } catch (Exception e) {
            // Invalid format or decoding error
            return Optional.empty();
        }
    }

    /**
     * Register a new user with a specific role.
     * Password is encrypted before storage for security.
     *
     * @param email User's email address
     * @param password User's password (plain text, will be encrypted)
     * @param name User's display name
     * @param role User's role (ADMIN, MANAGER, or USER)
     * @return The created User object
     */
    public User registerUser(String email, String password, String name, UserRole role) {
        validateRequired(email, "email");
        validateRequired(password, "password");
        validateRequired(name, "name");

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists.");
        }

        String encryptedPassword = PasswordEncryption.encrypt(password);
        UserRole assignedRole = role != null ? role : UserRole.USER;

        User newUser = new User(email, encryptedPassword, name, assignedRole);
        return userRepository.save(newUser);
    }

    /**
     * Register a new user with default USER role
     *
     * @param email User's email address
     * @param password User's password
     * @param name User's display name
     * @return The created User object
     */
    public User registerUser(String email, String password, String name) {
        return registerUser(email, password, name, UserRole.USER);
    }

    /**
     * Check if user exists
     */
    public boolean userExists(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.existsByEmail(email);
    }

    /**
     * Get all users
     */
    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Update user information.
     * Password is encrypted before storage if provided.
     *
     * @param email User's email address
     * @param password New password (plain text, will be encrypted), or null to keep current
     * @param name New name, or null to keep current
     * @return The updated User object, or null if user not found
     */
    public User updateUser(String email, String password, String name) {
        
        if (email == null || email.isBlank()) {
            return null;
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();

        if (password != null && !password.isBlank()) {
            String encrypted = PasswordEncryption.encrypt(password);
            user.setPassword(encrypted);
        }

        if (name != null && !name.isBlank()) {
            user.setName(name);
        }

        return userRepository.save(user);
    }

    /**
     * Delete user by email
     */
    public boolean deleteUser(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.deleteByEmail(email);
    }

    private void validateRequired(String value, String fieldName) {
    if (value == null || value.isBlank()) {
        throw new IllegalArgumentException("Field '" + fieldName + "' is required.");
    }
}
}
