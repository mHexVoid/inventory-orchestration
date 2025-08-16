package com.hexvoid.inv.orch.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handles custom ApiException (400 Bad Request).
	 */
	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
		return buildResponse("Bad Request", ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * Handles ResourceNotFoundException (404 Not Found).
	 */
	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return buildResponse("Not Found", ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	/**
	 * Handles UnauthorizedException (401 Unauthorized).
	 */
	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleUnAuthorizedException(UnauthorizedException ex) {
		return buildResponse("Access Denied", ex.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	/******************************************************************************
	 * Trying to Catch Exception under GlobalException Handler.
	 * 
	 * Note: This will not catch exceptions thrown at the filter level 
	 * (e.g., during Spring Security filter chain) since filters run 
	 * before the controller layer.
	 *****************************************************************************/

	/**
	 * Handles BadCredentialsException (401 Unauthorized).
	 *
	 */
	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
		return buildResponse("Unauthorized", ex.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	/**
	 * Handles AuthorizationDeniedException (403 Forbidden).
	 */
	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
		return buildResponse("Forbidden", ex.getMessage(), HttpStatus.FORBIDDEN);
	}

	/**
	 * Handles all uncaught exceptions (500 Internal Server Error).
	 * 
	 * Note: May cause issues with Springdoc/OpenAPI (v2.3.0).
	 * Needs further analysis for compatibility.
	 */
	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
		return buildResponse("Internal Server Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Builds a structured error response body.
	 *
	 * @param error   - Error type (e.g., "Bad Request")
	 * @param message - Detailed error message
	 * @param status  - Corresponding HTTP status code
	 * @return ResponseEntity with standardized error format
	 */
	private ResponseEntity<Map<String, Object>> buildResponse(String error, String message, HttpStatus status) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", System.currentTimeMillis());
		body.put("status", status.value());
		body.put("error", error);
		body.put("message", message);
		return new ResponseEntity<>(body, status);
	}
}
