package com.hexvoid.inv.orch.auth.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexvoid.inv.orch.auth.dto.AppUserDto;
import com.hexvoid.inv.orch.auth.dto.LoginRequest;
import com.hexvoid.inv.orch.auth.dto.LoginResponse;
import com.hexvoid.inv.orch.auth.entity.AppUser;
import com.hexvoid.inv.orch.auth.mapper.RegistrationMapper;
import com.hexvoid.inv.orch.auth.service.UserAuthService;
import com.hexvoid.inv.orch.constants.ApplicationConstants;
import com.hexvoid.inv.orch.logs.entity.AuditEventType;
import com.hexvoid.inv.orch.logs.service.AuditLogger;
import com.hexvoid.inv.orch.security.JwtTokenGenerator;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserAuthService userAuthService;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenGenerator jwtTokenGenerator;
	private final AuditLogger auditLogger;



	AuthController(UserAuthService userAuthService, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtTokenGenerator jwtTokenGenerator,
			AuditLogger auditLogger ) {

		this.userAuthService = userAuthService;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtTokenGenerator = jwtTokenGenerator;
		this.auditLogger = auditLogger;
	}



	// Sign up (validates unique email/username; hashed passwords).
	@PostMapping("/register")
	ResponseEntity<Map<String, Object>> doRegistration(@RequestBody AppUserDto appUserDto) {

		auditLogger.log(
				"UNKNOWN", AuditEventType.USER_REGISTER_ATTEMPT,
				"Registration attempt received for user: " + appUserDto.getUsername()
				);

		AppUser user = RegistrationMapper.toDAO(appUserDto);

		// Encode password
		auditLogger.log(
				"UNKNOWN", AuditEventType.USER_REGISTER_ATTEMPT,
				"Encoding password for user: " + user.getUsername()
				);

		user.setPassword(passwordEncoder.encode(appUserDto.getPassword()));

		// Persist to DB
		Optional<AppUser> savedUserDetails = Optional.of(userAuthService.save(user));

		if (savedUserDetails.isPresent()) {

			auditLogger.log(
					savedUserDetails.map(AppUser::getUsername).orElse(null),
					AuditEventType.USER_REGISTER_SUCCESS,
					"User " +savedUserDetails.map(AppUser::getUsername).orElse(null)+ 
					" successfully saved with ID:  "+savedUserDetails.map(AppUser::getId).orElse(null)
					);

			Map<String, Object> response = new HashMap<>();
			response.put("message", "User registered successfully");
			response.put("userId", savedUserDetails.map(AppUser::getId));

			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} else {
			auditLogger.log(
					savedUserDetails.map(AppUser::getUsername).orElse(null),
					AuditEventType.USER_REGISTER_FAILED,
					"Failed to save user: "+ appUserDto.getUsername() +" due to duplicate username or email" 
					);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(Map.of("error", "Registration failed", "details", "Handle this"));
		}
	}

	// Obtain JWT token for the session.
	@PostMapping("/login")
	ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

		// Create unauthenticated authentication token using username and password
		String userName = loginRequest.userName();
		String password = loginRequest.password();
		Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(userName,
				password);
		
		// Delegate authentication to custom provider via AuthenticationManager
		Authentication authenticationResponse = authenticationManager.authenticate(authentication);
		String jwt = jwtTokenGenerator.generateToken(authenticationResponse);

		auditLogger.log(userName, 
				AuditEventType.JWT_GENERATED_SUCCESS,
				"Adding JWT to HTTP Response Header");

		// Set JWT token in HTTP header
		HttpHeaders headers = new HttpHeaders();
		headers.set(ApplicationConstants.JWT_HEADER_NAME, jwt);

		// Create response body with status and token
		LoginResponse responseBody = new LoginResponse(HttpStatus.OK.getReasonPhrase(), jwt);

		auditLogger.log(loginRequest.userName(), 
				AuditEventType.JWT_GENERATED_SUCCESS, 
				"Login successful for user : "+ userName );

		return new ResponseEntity<>(responseBody, headers, HttpStatus.OK);

	}

	// Invalidate JWT (via blacklist or expiry).
	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request) {

		return ResponseEntity.ok("Logged out successfully.");

	}

}
