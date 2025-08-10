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

	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
		return buildResponse("Bad Request", ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return buildResponse("Not Found", ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleUnAuthorizedException(UnauthorizedException ex) {
		return buildResponse("Access Denied", ex.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleBadCredentialsException(BadCredentialsException ex) {
		return buildResponse("Unauthorized", ex.getMessage(), HttpStatus.UNAUTHORIZED);
	}

	//Causing issue on openApi with 2.3.0 working on new one Need to Analyze
	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
		return buildResponse("Internal Server Error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
		return buildResponse("Forbidden", ex.getMessage(), HttpStatus.FORBIDDEN);
	}

	private ResponseEntity<Map<String, Object>> buildResponse(String error, String message, HttpStatus status) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", System.currentTimeMillis());
		body.put("status", status.value());
		body.put("error", error);
		body.put("message", message);
		return new ResponseEntity<>(body, status);
	}
}
