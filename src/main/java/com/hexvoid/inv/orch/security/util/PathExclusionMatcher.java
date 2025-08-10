package com.hexvoid.inv.orch.security.util;

import java.util.List;

public class PathExclusionMatcher {

	private static final List<String> exactPaths = List.of(
			"/api/auth/login",
			"/api/auth/register"
			);

	private static final List<String> prefixPaths = List.of(
			"/swagger-ui",
			"/v3/api-docs",
			"/swagger-resources",
			"/webjars",
			"/swagger-ui.html",
			"/favicon.ico"
			);

	public static boolean shouldExclude(String requestUri) {

		boolean exactMatchPaths = exactPaths.contains(requestUri);
		boolean prefixMatchedPaths = prefixPaths.stream().anyMatch(requestUri::startsWith);

		System.out.printf(
				"Request URI: %s | Exact match: %b | Prefix match: %b%n",
				requestUri, exactMatchPaths, prefixMatchedPaths
				);


		return exactMatchPaths || prefixMatchedPaths;
	}
}
