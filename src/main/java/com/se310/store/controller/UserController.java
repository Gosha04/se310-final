package com.se310.store.controller;

import com.se310.store.dto.UserMapper;
import com.se310.store.dto.UserMapper.UserDTO;
import com.se310.store.model.User;
import com.se310.store.model.UserRole;
import com.se310.store.service.AuthenticationService;
import com.se310.store.servlet.BaseServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * REST API controller for User operations
 * Implements full CRUD operations using DTO Pattern
 *
 * DTOs are used to:
 * - Hide sensitive information (passwords) from API responses
 * - Provide a clean separation between internal domain models and external API contracts
 * - Allow API responses to evolve independently from internal data structures
 *
 * @author  Sergey L. Sundukovskiy
 * @version 1.0
 * @since   2025-11-11
 */
public class UserController extends BaseServlet {

    //TODO: Implement Controller for User operations, part of the MVC Pattern

    private final AuthenticationService authenticationService;

    public UserController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Handle GET requests - Returns UserDTO objects (without passwords)
     * - GET /api/v1/users (no parameters) - Get all users
     * - GET /api/v1/users/{email} - Get user by email
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = extractResourceId(request);

        if (email == null) {
            Collection<User> users = authenticationService.getAllUsers();
            var dtoList = UserMapper.toDTOList(users);
            sendJsonResponse(response, dtoList, HttpServletResponse.SC_OK);
            return;
        }

        User user = authenticationService.getUserByEmail(email);
        if (user == null) {
            sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }
        UserDTO userDTO = UserMapper.toDTO(user);
        sendJsonResponse(response, userDTO, HttpServletResponse.SC_OK);
    }

    /**
     * Handle POST requests - Register new user, returns UserDTO (without password)
     * POST /api/v1/users?email=xxx&password=xxx&name=xxx&role=xxx
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String roleParam = request.getParameter("role");

        if (email == null || email.isBlank() ||
            password == null || password.isBlank() ||
            name == null || name.isBlank() ||
            roleParam == null || roleParam.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Missing required parameters");
            return;
        }

        UserRole role = UserRole.USER;
        if (roleParam != null && !roleParam.isBlank()) {
            try {
                role = UserRole.valueOf(roleParam.toUpperCase());
            } catch (IllegalArgumentException ex) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid role: " + roleParam + ". Expected ADMIN, MANAGER, or USER.");
                return;
            }
        }


        try {
            User created = authenticationService.registerUser(email, password, name, role);
            UserDTO dto = UserMapper.toDTO(created);
            sendJsonResponse(response, dto, HttpServletResponse.SC_CREATED);
        } catch (IllegalArgumentException ex) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error registering user: " + ex.getMessage());
        }

    }

    /**
     * Handle PUT requests - Update user information, returns UserDTO (without password)
     * PUT /api/v1/users/{email}?password=xxx&name=xxx
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = extractResourceId(request);

        if (email == null || email.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "User email must be provided");
            return;
        }

        String newPassword = request.getParameter("password");
        String newName = request.getParameter("name");

        try {
            User updatedUser = authenticationService.updateUser(email, newPassword, newName);
            if (updatedUser == null) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            UserDTO dto = UserMapper.toDTO(updatedUser);
            sendJsonResponse(response, dto, HttpServletResponse.SC_OK);
        } catch (Exception ex) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error updating user: " + ex.getMessage());
        }
    }

    /**
     * Handle DELETE requests - Delete user
     * DELETE /api/v1/users/{email}
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = extractResourceId(request);

        if (email == null || email.isBlank()) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,
                    "User email must be provided");
            return;
        }

        try {
            boolean deleted = authenticationService.deleteUser(email);
            if (!deleted) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            sendJsonResponse(response, null, HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception ex) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error deleting user: " + ex.getMessage());
        }
    }
}