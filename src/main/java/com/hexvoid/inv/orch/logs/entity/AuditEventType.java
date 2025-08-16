package com.hexvoid.inv.orch.logs.entity;

public enum AuditEventType {

	// Auth events
	AUTH_ATTEMPT,
	AUTH_SUCCESS,
	AUTH_FAILURE,

	JWT_GENERATE_ATTEMPT,       // Attempting to generate token (usually after auth success)
	JWT_GENERATED_SUCCESS,      // Token generated successfully
	JWT_GENERATED_FAILURE,      // Failed to generate token (unexpected error etc.)
	JWT_VALIDATION_ATTEMPT,     // Incoming request token being validated
	JWT_VALIDATION_SUCCESS,     // Token is valid
	JWT_VALIDATION_FAILURE,     // Invalid token (wrong signature, malformed, etc.)

	JWT_LOGIN_ATTEMPT,
	JWT_LOGIN_SUCCESS,
	JWT_LOGIN_FAILURE,

	JWT_LOGOUT_ATTEMPT,
	JWT_LOGOUT_SUCCESS,
	JWT_LOGOUT_FAILURE,
	JWT_EXPIRED,                // Token expired (detected while validating)

	// User registration events
	USER_REGISTER_ATTEMPT,
	USER_REGISTER_SUCCESS,
	USER_REGISTER_FAILED,

	// Product events
	PRODUCT_CREATE,
	PRODUCT_UPDATE,

	// Order events
	ORDER_CREATE,
	ORDER_PURCHASE_RECEIVED,

	// Forecast events
	FORECAST_EXECUTE,

	// Secret key / key management events
	SECRET_KEY_CREATE_ATTEMPT,  // Attempt to create a new key
	SECRET_KEY_CREATE_SUCCESS,  // Key created successfully
	SECRET_KEY_CREATE_FAILURE,  // Failed to create key
	SECRET_KEY_ROTATE_ATTEMPT,  // Attempt to rotate key
	SECRET_KEY_ROTATE_SUCCESS,  // Key rotated successfully
	SECRET_KEY_ROTATE_FAILURE,  // Failed to rotate key
	SECRET_KEY_REVOKE_ATTEMPT,  // Attempt to revoke key
	SECRET_KEY_REVOKE_SUCCESS,  // Key revoked successfully
	SECRET_KEY_REVOKE_FAILURE,   // Failed to revoke key

	SECRET_KEY_LOAD_FAILURE,
	SECRET_KEY_LOAD_WARNING,
	SECRET_KEY_LOAD_SUCCESS
}
