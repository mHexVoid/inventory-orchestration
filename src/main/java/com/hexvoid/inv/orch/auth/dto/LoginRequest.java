package com.hexvoid.inv.orch.auth.dto;

/**
 * A record that encapsulates the login credentials submitted by a user.
 * <p>
 * This record is used as the request body for the authentication endpoint.
 * It holds the username and password provided during login.
 *
 * <p>Being a {@code record}, it is immutable and automatically provides
 * accessor methods for each field.
 *
 * @param userName the username or email used for authentication
 * @param password the corresponding password
 */
public record LoginRequest(String userName, String password) {
}
