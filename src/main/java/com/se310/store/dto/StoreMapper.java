package com.se310.store.dto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.se310.store.model.Store;

/**
 * StoreMapper implements the DTO Pattern for Store entities.
 * Provides transformation between Store domain objects and DTOs to separate
 * internal representation from API responses (excludes transient collections for cleaner JSON).
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 * @since   2025-11-11
 */
public class StoreMapper{

    //TODO: Implement Data Transfer Object for Store entity
    //TODO: Implement Factory methods for Store DTOs

    /**
     * StoreDTO - Data Transfer Object for Store
     */
    public static class StoreDTO implements JsonSerializable{
        private String id;
        private String address;
        private String description;

        public StoreDTO() {
        }

        public StoreDTO(String id, String address, String description) {
            this.id = id;
            this.address = address;
            this.description = description;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
        @Override
        public String toJson() {
            return JsonHelper.toJson(this);
        }
        
    }

    public static StoreDTO toDTO(Store store) {
            if (store == null) {
                return null;
            }
            return new StoreDTO(
                    store.getId(),
                    store.getAddress(),
                    store.getDescription()
            );
        }
        public static List<StoreDTO> toDTOList(Collection<Store> stores) {
            if (stores == null) {
                return List.of();
            }
            return stores.stream()
                .map(StoreMapper::toDTO)
                .collect(Collectors.toList());
        }
        
        public static Store fromDTO(StoreDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Store(
                dto.getId(),
                dto.getAddress(),
                dto.getDescription()
        );
        }
        public static void updateStoreFromDTO(Store store, StoreDTO dto) {
            if (store == null || dto == null) {
                return;
            }
            store.setAddress(dto.getAddress());
            store.setDescription(dto.getDescription());
        }
}
