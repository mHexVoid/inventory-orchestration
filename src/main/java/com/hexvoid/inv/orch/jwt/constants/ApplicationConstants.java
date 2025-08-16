package com.hexvoid.inv.orch.jwt.constants;


/**
 * This class contains all application-level constants that are commonly used
 * throughout the Employee Portal project — especially related to JWT security
 * configurations such as secret key management and header naming.
 *
 * <p>
 * These constants help ensure consistent usage across the codebase, avoid magic
 * strings, and make the application configuration easier to manage, especially
 * across different environments (e.g., local, staging, production).
 * </p>
 *
 * <h3>JWT_SECRET_KEY</h3>
 * <ul>
 *     <li>This is the environment variable key that should hold the secret used to sign and verify JWT tokens.</li>
 *     <li>In production, this <b>must</b> be injected via environment or vault-based secret managers.</li>
 *     <li>Failing to provide a valid key may lead to insecure token signing.</li>
 * </ul>
 *
 * <h3>JWT_DEFAULT_SECRET_VALUE</h3>
 * <ul>
 *     <li>This fallback secret will be used only if no environment variable is found for JWT_SECRET_KEY.</li>
 *     <li><b>NOTE:</b> This should only be used in development or testing environments to avoid security risks.</li>
 *     <li>In production, this fallback should either be disabled or the application should throw an exception if no secret is configured.</li>
 * </ul>
 *
 * <h3>JWT_HEADER_NAME</h3>
 * <ul>
 *     <li>This defines the standard HTTP header through which the JWT will be transmitted between client and server.</li>
 *     <li>Used both in request headers (client → server) and response headers (server → client).</li>
 *     <li>The most common standard name for this is "Authorization".</li>
 * </ul>
 *
 * <p><b>Example usage for local development (in application.properties or as ENV var):</b></p>
 * <pre>
 *     # As system environment variable or in application.properties
 *     jwt.secretKey=a8VYmKz7nL5xQ2pNfJrGcTeWvBdYzXh9UsMwEnRbTqOjSl3K
 * </pre>
 *
 * <p><b>Security Tip:</b> Never expose the actual value of secrets in public repositories.</p>
 */
public final class ApplicationConstants {

	/** 
	 * Environment variable key used to fetch the JWT signing/validation secret.
	 */
	public static final String JWT_SECRET_KEY = "jwt.secretKey";

	/**
	 * Default fallback JWT secret value — used if no env variable is provided.
	 * 
	 * WARNING: Use only in local development/testing environments.
	 * In production, secret must always be injected securely.
	 */
	public static final String JWT_DEFAULT_SECRET_VALUE =
			"ThisCanBeAnythingInCaseIfNoSecretKeyIsProvidedFromEnvVarIt'sFallbackToDefaultvalue";

	/**
	 * HTTP header name used to transmit JWT tokens in client-server communication.
	 */
	public static final String JWT_HEADER_NAME = "Authorization";


	/**
	 * Example of setting the JWT secret securely as environment variable (Do not hardcode secrets):
	 *
	 * jwt.secretKey=a8VYmKz7nL5xQ2pNfJrGcTeWvBdYzXh9UsMwEnRbTqOjSl3K
	 */
}
