package com.hexvoid.inv.orch.auth.dto;


/**
 * A record that represents the response returned upon successful authentication.
 * <p>
 * It contains the status of the login request and the generated JWT token.
 * The token can be used for authorized access to secured endpoints.
 *
 * <p>Being a {@code record}, it is immutable and automatically provides
 * accessor methods for each field.
 *
 * @param status the HTTP status or message of the login result
 * @param jwtToken the generated JWT token issued to the client
 */
public record LoginResponse(String status, String jwtToken) {
}
