

package com.hexvoid.inv.orch.security.jwt;

import org.springframework.stereotype.Component;

import com.hexvoid.inv.orch.security.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtService {

	private final JwtConfig jwtConfig;

	public JwtService(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}

	public Claims parseClaims(String token) {

		return Jwts.parser()
				.verifyWith(jwtConfig.getSecretKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}