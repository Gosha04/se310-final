package com.se310.store.dto;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.List;
import com.se310.store.model.User;
import com.se310.store.model.UserRole;

/**
 * UserMapper implements the DTO Pattern for User entities.
 * Provides transformation between User domain objects and DTOs to separate
 * internal representation from API responses (e.g., hiding sensitive data like passwords).
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 * @since   2025-11-11
 */
public class UserMapper {

    //TODO: Implement Data Transfer Object for User entity
    //TODO: Implement Factory methods for User DTOs

    /**
     * UserDTO - Data Transfer Object for User
     */
    public static class UserDTO {
        private String email;
        private String name;
        private String role;

        public UserDTO() {
        }

        public UserDTO(String email, String name, String role) {
            this.email = email;
            this.name = name;
            this.role = role;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getEmail(),
                user.getName(),
                user.getRole() != null ? user.getRole().name() : null
        );
    }
    public static List<UserDTO> toDTOList(Collection<User> users) {
        if (users == null) {
            return List.of();
        }
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static User fromDTO(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        UserRole role = UserRole.USER;
        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            try {
                role = UserRole.valueOf(dto.getRole().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                role = UserRole.USER;
            }
        }

        return new User(
                dto.getEmail(),
                null, 
                dto.getName(),
                role
        );

    }
    public static void updateUserFromDTO(User user, UserDTO dto) {
        if (user == null || dto == null) {
            return;
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            user.setEmail(dto.getEmail());
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            try {
                user.setRole(UserRole.valueOf(dto.getRole().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
