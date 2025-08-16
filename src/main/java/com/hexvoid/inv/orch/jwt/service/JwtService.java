package com.hexvoid.inv.orch.jwt.service;

import java.time.Instant;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;

public interface JwtService {

	//for parsing JWT
	SecretKey getSigningKey();
	String extractToken(String authHeader);                       // "Bearer x" -> x
	Claims parseAndValidate(String token , SecretKey key);       // throws on invalid/expired

	//for extracting claims
	String getEmail(Claims c);                        // from custom claim or subject
	String getAuthorities(Claims c);                  // custom claim if you set it
	String getJti(Claims c);                          // c.getId()
	Instant getExpiry(Claims c);                      // c.getExpiration().toInstant()

	//Blacklist Service
	void validateNotBlacklisted(String token);

	//Claims parseAndValidate(String token);
	//Claims parseToken(String token);

	Long getUserId(Claims c);

}
