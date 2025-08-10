package com.hexvoid.inv.orch.security.jwt;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import com.hexvoid.inv.orch.security.service.JwtTokenBlacklistService;


@Component
public class JwtTokenBlacklistChecker {

	private final JwtTokenBlacklistService blacklistService;

	public JwtTokenBlacklistChecker(JwtTokenBlacklistService blacklistService) {
		this.blacklistService = blacklistService;
	}

	public void check(String token ) {

		if (blacklistService.isTokenBlacklisted(token)) {
			throw new BadCredentialsException("Token blacklisted");
		}
	}
}