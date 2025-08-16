package com.hexvoid.inv.orch.jwt.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import javax.crypto.SecretKey;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import com.hexvoid.inv.orch.jwt.constants.ApplicationConstants;
import com.hexvoid.inv.orch.jwt.constants.JwtClaimNames;
import com.hexvoid.inv.orch.logs.entity.AuditEventType;
import com.hexvoid.inv.orch.logs.service.AuditLogRouter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtServiceImpl implements JwtService {

	private final JwtTokenBlacklistService jwtTokenBlacklistService;
	private final AuditLogRouter auditLogRouter;
	private final Environment environment;

	public JwtServiceImpl(JwtTokenBlacklistService jwtTokenBlacklistService, AuditLogRouter auditLogRouter,
			Environment environment) {

		this.jwtTokenBlacklistService = jwtTokenBlacklistService;
		this.auditLogRouter = auditLogRouter;
		this.environment = environment;
	}

	@Override
	public String getEmail(Claims c) {
		return  (String) c.get(JwtClaimNames.EMAIL);
	}

	@Override
	public String getAuthorities(Claims c) {
		return String.valueOf(c.get(JwtClaimNames.AUTHORITIES));
	}

	@Override
	public String getJti(Claims c) {
		return c.getId();
	}

	@Override
	public Instant getExpiry(Claims c) {
		return c.getExpiration().toInstant();
	}

	@Override
	public SecretKey getSigningKey() {

		String jwtSecretKey = ApplicationConstants.JWT_SECRET_KEY;
		String jwtDefaultSecretValue = ApplicationConstants.JWT_DEFAULT_SECRET_VALUE;

		String property = environment.getProperty(jwtSecretKey,
				jwtDefaultSecretValue);

		if(property==null||property.isBlank()) {

			auditLogRouter.performLogsOperation(
					null,
					AuditEventType.SECRET_KEY_LOAD_FAILURE, 
					"JWT signing key is missing or blank"
					);

			throw new IllegalStateException("Unable to get Signing Key");
		}
		else if(property.equals(jwtDefaultSecretValue)) {

			auditLogRouter.performLogsOperation(null, 
					AuditEventType.SECRET_KEY_LOAD_WARNING, 
					"System is Running on Defauly Key, Not Advisable for Prod Application"
					);
		}

		auditLogRouter.performLogsOperation(
				null, 
				AuditEventType.SECRET_KEY_LOAD_SUCCESS,
				"JWT signing key loaded successfully");

		return  Keys.hmacShaKeyFor(property.getBytes(StandardCharsets.UTF_8));
	}



	@Override
	public String extractToken(String request) {

		if(request==null) {
			throw new BadCredentialsException("Missing Access Token: Unable to perform Any Operation");
		}
		return request;
	}

	@Override
	public Claims parseAndValidate(String token, SecretKey key) {

		return Jwts.parser().
				verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	@Override
	public void validateNotBlacklisted(String token) {
		if(jwtTokenBlacklistService.isTokenBlacklisted(token)) {
			throw new BadCredentialsException("Token is BlackListed");
		}
	}

	@Override
	public Long getUserId(Claims c) {
		// TODO Auto-generated method stub
		return null;
	}

}
