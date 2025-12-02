package com.se310.store.controller;

import com.se310.store.dto.StoreMapper;
import com.se310.store.dto.StoreMapper.StoreDTO;
import com.se310.store.model.Store;
import com.se310.store.model.StoreException;
import com.se310.store.service.StoreService;
import com.se310.store.servlet.BaseServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;


/**
 * REST API controller for Store operations
 * Implements full CRUD operations using DTO Pattern
 *
 * DTOs are used to:
 * - Simplify API responses by excluding complex nested collections
 * - Provide a clean separation between internal domain models and external API contracts
 * - Improve JSON serialization performance by excluding transient fields
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 * @since   2025-11-11
 */
public class StoreController extends BaseServlet {

    //TODO: Implement Controller for Store operations, part of the MVC Pattern

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    /**
     * Handle GET requests - Returns StoreDTO objects
     * - GET /api/v1/stores (no parameters) - Get all stores
     * - GET /api/v1/stores/{storeId} - Get store by ID
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String storeId = extractResourceId(request);
        String token = request.getParameter("token");

        if (storeId == null || storeId.isBlank()) {
            Collection<Store> stores = storeService.getAllStores();
            var dtoList = StoreMapper.toDTOList(stores);
            sendJsonResponse(response, dtoList, HttpServletResponse.SC_OK);
            return;
        }

        try {
            Store store = storeService.showStore(storeId, token);
            StoreDTO storeDTO = StoreMapper.toDTO(store);
            sendJsonResponse(response, storeDTO, HttpServletResponse.SC_OK);

        } catch (StoreException ex) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
        catch (Exception ex) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Handle POST requests - Create new store, returns StoreDTO
     * POST /api/v1/stores?storeId=xxx&name=xxx&address=xxx
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String storeId = request.getParameter("storeId");
        String name = request.getParameter("name");       
        String address = request.getParameter("address");
        String token = request.getParameter("token");

        if (storeId == null || storeId.isBlank()
                || name == null || name.isBlank()
                || address == null || address.isBlank()) {

            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Missing required parameters: storeId, name, and address are required.");
            return;
        }

        try {
            Store store = storeService.provisionStore(storeId, name, address, token);
            StoreDTO storeDTO = StoreMapper.toDTO(store);
            sendJsonResponse(response, storeDTO, HttpServletResponse.SC_CREATED);

        } catch (StoreException ex) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    /**
     * Handle PUT requests - Update existing store, returns StoreDTO
     * PUT /api/v1/stores/{storeId}?description=xxx&address=xxx
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String storeId = extractResourceId(request);

        if (storeId == null || storeId.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing storeId in the URL path.");
            return;
        }
        String description = request.getParameter("description");
        String address = request.getParameter("address");

        if ((description == null || description.isBlank())
                && (address == null || address.isBlank())) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "At least one of description or address parameters must be provided for update.");
            return;
        }

        try {
            Store store = storeService.updateStore(storeId, description, address);
            StoreDTO storeDTO = StoreMapper.toDTO(store);
            sendJsonResponse(response, storeDTO, HttpServletResponse.SC_OK);

        } catch (StoreException ex) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpectd error updating store");
        }

    }

    /**
     * Handle DELETE requests - Delete store
     * DELETE /api/v1/stores/{storeId}
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String storeId = extractResourceId(request);

        if (storeId == null || storeId.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing storeId in the URL path.");
            return;
        }

        try {
            storeService.deleteStore(storeId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (StoreException ex) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error deleting store");
        }
    }
}