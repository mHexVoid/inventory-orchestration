package com.hexvoid.inv.orch.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.hexvoid.inv.orch.constants.ApplicationConstants;
import com.hexvoid.inv.orch.logs.entity.AuditEventType;
import com.hexvoid.inv.orch.logs.service.AuditLogger;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenGenerator {

	private final Environment environment;
	private final AuditLogger auditLogger;

	public JwtTokenGenerator(Environment environment , AuditLogger auditLogger ) {
		this.environment = environment;
		this.auditLogger=auditLogger;
	}

	public String generateToken(Authentication authenticationResponse) {

		String jwt = "";
		String name = authenticationResponse.getName();
		//Optional<AppUser> appUser = Optional.of(userAuthService.findByUserName(name));

		auditLogger.log(name, 
				AuditEventType.JWT_GENERATE_ATTEMPT, 
				"Attempting to generated JWT for user "+name
				);

		try {

			if (environment != null) {

				String secret = environment.getProperty(
						ApplicationConstants.JWT_SECRET_KEY,
						ApplicationConstants.JWT_DEFAULT_SECRET_VALUE);

				auditLogger.log(name, 
						AuditEventType.JWT_GENERATE_ATTEMPT, 
						"Fetching Secrect Properties for Key Generation"
						);

				SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

				auditLogger.log(name, 
						AuditEventType.JWT_GENERATE_ATTEMPT, 
						"Generated Secret Key Successfully : Moving for JWT Generation");

				jwt = Jwts.builder()
						.issuer("Inventory Orchestration App")
						.subject("JWT Token")
						.claim("email", authenticationResponse.getName())
						.claim("authorities", authenticationResponse.getAuthorities().stream()
								.map(grantedAuthority -> grantedAuthority.getAuthority())
								.collect(Collectors.joining(",")))
						.issuedAt(new Date())
						.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hrs
						.signWith(secretKey)
						.compact();

				auditLogger.log(name, 
						AuditEventType.JWT_GENERATED_SUCCESS, 
						"JWT Successfully Generated for User : "+name);
			}


		}
		catch (InvalidKeyException e) {

			auditLogger.log(name, 
					AuditEventType.JWT_GENERATED_FAILURE, 
					e.getMessage());
		}
		return jwt;
	}
}



