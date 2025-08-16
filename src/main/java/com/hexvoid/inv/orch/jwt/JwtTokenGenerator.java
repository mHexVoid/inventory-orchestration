package com.hexvoid.inv.orch.jwt;

import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.hexvoid.inv.orch.jwt.constants.JwtClaimNames;
import com.hexvoid.inv.orch.jwt.service.JwtService;
import com.hexvoid.inv.orch.logs.entity.AuditEventType;
import com.hexvoid.inv.orch.logs.service.AuditLogRouter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;

@Component
public class JwtTokenGenerator {

	private final JwtService jwtService;
	private final AuditLogRouter auditLogRouter;

	public JwtTokenGenerator(JwtService jwtService, AuditLogRouter auditLogRouter) {
		this.jwtService = jwtService;
		this.auditLogRouter = auditLogRouter;
	}

	public String generateToken(Authentication authenticationResponse) {

		String jwt = null;
		String name = authenticationResponse.getName();
		//Optional<AppUser> appUser = Optional.of(userAuthService.findByUserName(name));

		auditLogRouter.performLogsOperation(name, 
				AuditEventType.JWT_GENERATE_ATTEMPT, 
				"Attempting to generated JWT for user "+name
				);

		try {

			auditLogRouter.performLogsOperation(name, 
					AuditEventType.JWT_GENERATE_ATTEMPT, 
					"Fetching Secrect Properties for Key Generation"
					);

			SecretKey secretKey = jwtService.getSigningKey();


			auditLogRouter.performLogsOperation(name, 
					AuditEventType.JWT_GENERATE_ATTEMPT, 
					"Generated Secret Key Successfully : Moving for JWT Generation");

			jwt = Jwts.builder()
					.issuer("hexvoid-orch")
					.subject("JWT Token")
					.claim(JwtClaimNames.EMAIL, authenticationResponse.getName())
					.claim(JwtClaimNames.AUTHORITIES, authenticationResponse.getAuthorities().stream()
							.map(grantedAuthority -> grantedAuthority.getAuthority())
							.collect(Collectors.joining(",")))
					.issuedAt(new Date())
					.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hrs
					.signWith(secretKey)
					.compact();

			auditLogRouter.performLogsOperation(name, 
					AuditEventType.JWT_GENERATED_SUCCESS, 
					"JWT Successfully Generated for User : "+name);

		}
		catch (InvalidKeyException e) {
			auditLogRouter.performLogsOperation(name, 
					AuditEventType.JWT_GENERATED_FAILURE, 
					e.getMessage());
		}
		return jwt;
	}
}



