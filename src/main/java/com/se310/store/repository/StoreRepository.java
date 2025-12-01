package com.se310.store.repository;

import com.se310.store.data.DataManager;
import com.se310.store.model.Store;

import java.util.*;

/**
 * Store Repository implements Repository Pattern for store data access layer
 * Uses DataManager for persistent storage
 *
 * This repository is completely database-agnostic - it has no knowledge of SQL,
 * ResultSets, or SQLExceptions. All database-specific logic is encapsulated in DataManager.
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 * @since   2025-11-06
 */
public class StoreRepository {

    //TODO: Implement Store persistence layer using Repository Pattern

    private final DataManager dataManager;

    public StoreRepository(DataManager dataManager) {

        this.dataManager = dataManager;
    }
    public Store save(Store store) {
        return dataManager.persistStore(store);
    }

    public Optional<Store> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return dataManager.getStoreById(id);
    }

    public Collection<Store> findAll() {
        List<Store> stores = dataManager.getAllStores();
        return stores;
    }

    public boolean existsById(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        return dataManager.doesStoreExist(id);
    }

    public void delete(Store store) {
        if (store == null || store.getId() == null) {
            return;
        }
        dataManager.removeStore(store.getId());
    }

    public boolean deleteById(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        return dataManager.removeStore(id);
    }
}