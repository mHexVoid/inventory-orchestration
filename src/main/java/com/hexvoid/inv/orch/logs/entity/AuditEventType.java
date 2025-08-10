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
    JWT_VALIDATION_FAILURE,     // / Invalid token (wrong signature, malformed, etc.)
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
    FORECAST_EXECUTE
		
}
