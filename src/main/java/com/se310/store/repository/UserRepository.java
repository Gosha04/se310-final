package com.se310.store.repository;

import com.se310.store.data.DataManager;
import com.se310.store.model.User;

import java.util.*;

/**
 * User Repository implements Repository Pattern for user data access layer
 * Uses DataManager for persistent storage
 *
 * This repository is completely database-agnostic - it has no knowledge of SQL,
 * ResultSets, or SQLExceptions. All database-specific logic is encapsulated in DataManager.
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 * @since   2025-11-06
 */
public class UserRepository {

    //TODO: Implement User persistence layer using Repository Pattern

    private final DataManager dataManager;

    public UserRepository(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    public User save(User user) {
        return dataManager.persistUser(user);
    }
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return dataManager.getUserByEmail(email);
    }
    public Collection<User> findAll() {
        return dataManager.getAllUsers();
    }

    public boolean existsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return dataManager.doesUserExist(email);
    }
    public void delete(User user) {
        if (user == null || user.getEmail() == null) {
            return;
        }
        dataManager.removeUser(user.getEmail());
    }

    public boolean deleteByEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return dataManager.removeUser(email);
    }
}