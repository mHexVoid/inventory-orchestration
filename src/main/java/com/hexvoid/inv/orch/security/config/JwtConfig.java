package com.hexvoid.inv.orch.security.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.hexvoid.inv.orch.constants.ApplicationConstants;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtConfig {

	private final Environment environment;

	public JwtConfig(Environment environment) {
		this.environment=environment;
	}

	public SecretKey getSecretKey() {

		String jwtSecret = environment.getProperty(
				ApplicationConstants.JWT_SECRET_KEY, 
				ApplicationConstants.JWT_DEFAULT_SECRET_VALUE);

		if(jwtSecret==null) {
			throw new IllegalStateException("JWT secret is not configured properly");
		}

		return Keys.hmacShaKeyFor(jwtSecret
				.getBytes(StandardCharsets.UTF_8));
	}

}
