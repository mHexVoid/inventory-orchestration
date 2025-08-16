package com.hexvoid.inv.orch.jwt.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class JwtTokenBlacklistService {

	private final Set<String> blacklist = ConcurrentHashMap.newKeySet(); // Or use Redis in prod

	public void blacklistToken(String token) {
		blacklist.add(token);
	}

	public boolean isTokenBlacklisted(String token) {
		return blacklist.contains(token);
	}

}
