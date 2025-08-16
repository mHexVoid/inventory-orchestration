package com.hexvoid.inv.orch.security.util;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * Central registry of all secured and open API endpoints.
 * Used by SecurityFilterChain for configuring Spring Security.
 */
public final class SecurityPathRegistry  {

	private final static Logger logger = LoggerFactory.getLogger(SecurityPathRegistry.class);

	// Publicly accessible paths (no authentication)
	public static final List<String> PUBLIC_ERROR_PATHS = List.of(
			"/error", "/error/**"
			);

	public static final List<String> PUBLIC_SWAGGER_PATHS = List.of(
			"/swagger-ui/**", "/v3/api-docs/**"
			);

	public static final List<String> PUBLIC_API_PATHS = List.of(
			"/api/auth/login", "/api/auth/register"
			);

	//	// Authenticated-only paths (no specific role needed)
	//	public static final List<String> AUTHENTICATED_API_PATHS = List.of(
	//			"/api/auth/logout"
	//			);

	// Role-based access control paths
	public static final Map<String, List<String>> ROLE_BASED_API_PATHS = Map.of(
			"/api/products", List.of("ADMIN"),
			"/api/products/**", List.of("ADMIN", "USER")
			);

	// Prevent instantiation
	private SecurityPathRegistry() {

	}

	public static Object applyAccessRules(
			AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {

		registerPermitAllEndpoints(registry);

		//registry.requestMatchers(AUTHENTICATED_API_PATHS.toArray(new String[0])).authenticated();

		ROLE_BASED_API_PATHS.forEach((path, roles) ->
		registry.requestMatchers(path).hasAnyRole(roles.toArray(String[]::new)));

		logAccessMap();

		return registry;
	}

	private static void registerPermitAllEndpoints(
			AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {

		List<List<String>> allPublicPaths = List.of(
				PUBLIC_API_PATHS,
				PUBLIC_ERROR_PATHS,
				PUBLIC_SWAGGER_PATHS
				);

		allPublicPaths.stream()
		.flatMap(List::stream)
		.forEach(getPermitAllConsumer(registry));

	}


	private static Consumer<String> getPermitAllConsumer(
			AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry customizeEndpoints) {

		return path -> customizeEndpoints.requestMatchers(path).permitAll();
	}


	static void logAccessMap(){

		List<List<String>> allPublicPaths = List.of(
				PUBLIC_API_PATHS,
				PUBLIC_ERROR_PATHS,
				PUBLIC_SWAGGER_PATHS
				);

		logger.debug("[All Public Endpoint Paths :]  "+ allPublicPaths );
		//logger.debug("[All Authenticated Endpoint Paths :]  "+ AUTHENTICATED_API_PATHS);
		logger.debug("[All Role Based API Paths :]  "+ ROLE_BASED_API_PATHS );



		String pattern="| %-30s | %-30s | %-30s |%n";
		String rowBorder="+--------------------------------+--------------------------------+--------------------------------+";
		System.out.println(rowBorder);
		System.out.printf(pattern,"Access Type","Endpoint","Allowed Roles");
		System.out.println(rowBorder);

		allPublicPaths.stream()
		.flatMap(List::stream)
		.forEach(t->System.out.printf(pattern, "PUBLIC",t,"-"));

		System.out.println(rowBorder);

		//AUTHENTICATED_API_PATHS.stream().forEach(t->System.out.printf(pattern, "AUTHENTICATED",t,"-"));

		System.out.println(rowBorder);

		ROLE_BASED_API_PATHS
		.forEach(
				(key,value)->
				System.out.printf(pattern , "ROLE BASED", key, value)
				);

		System.out.println(rowBorder);
	}
}
