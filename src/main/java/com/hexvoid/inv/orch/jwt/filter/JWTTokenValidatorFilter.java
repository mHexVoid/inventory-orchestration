package com.hexvoid.inv.orch.jwt.filter;

import java.io.IOException;

import javax.crypto.SecretKey;

import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hexvoid.inv.orch.jwt.constants.ApplicationConstants;
import com.hexvoid.inv.orch.jwt.service.JwtService;
import com.hexvoid.inv.orch.logs.entity.AuditEventType;
import com.hexvoid.inv.orch.logs.service.AuditLogRouter;
import com.hexvoid.inv.orch.security.service.SpringAuthenticationSetter;
import com.hexvoid.inv.orch.security.util.PathExclusionMatcher;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This filter is responsible for validating incoming JWT tokens from client requests.
 *
 * <p>
 * It is applied to all endpoints <strong>except</strong> <code>/user/details</code> (where the token is generated).
 * <br>
 * This filter:
 * <ul>
 *     <li>Extracts the token from the <code>Authorization</code> header</li>
 *     <li>Parses and validates the token using the secret key</li>
 *     <li>Extracts user details and sets them in Spring Security’s context</li>
 * </ul>
 * Once the context is set, Spring Security handles authorization based on roles/authorities.
 * </p>
 */
@Component
public class JWTTokenValidatorFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final SpringAuthenticationSetter springauthSetter;
	private final AuditLogRouter auditLogRouter;
	//private final AuditContextProvider auditContextProvider;

	public JWTTokenValidatorFilter(JwtService jwtService, SpringAuthenticationSetter springauthSetter,
			AuditLogRouter auditLogRouter) {
		this.jwtService = jwtService;
		this.springauthSetter = springauthSetter;
		this.auditLogRouter = auditLogRouter;
	}

	/**
	 * Intercepts every request before controller execution to:
	 * <ul>
	 *     <li>Validate the JWT token and parse (if present in the request header.)</li>
	 *     <li>Extract user data and set authentication in SecurityContext</li>
	 * </ul>
	 *
	 * @param request     the incoming HTTP request from the client
	 * @param response    the outgoing HTTP response
	 * @param filterChain filter chain to pass request/response to the next filter
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an input/output error occurs
	 */

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String email = null;
		String header = request.getHeader(ApplicationConstants.JWT_HEADER_NAME);

		try {
			// Step 1: Extract the JWT token from the Authorization header
			String token = jwtService.extractToken(header);

			//Step 2: Retrieve secret key from environment or fallback default
			// && Parse and validate the JWT token
			SecretKey sercetKey = jwtService.getSigningKey();

			Claims claims = jwtService.parseAndValidate(token, sercetKey);

			//Step 3 : Extract user information and authorities from the Claim
			/***
			 * email = String.valueOf(claims.get("email"));
			 * String authorities = String.valueOf(claims.get("authorities"));
			 */

			email = jwtService.getEmail(claims);
			String authorities = jwtService.getAuthorities(claims);

			//Step 4 : Get The UserName for Logging Purpose
			// @NOTE : Centralized the logic to get USER Entity Under the Logger : to Minimize multiple calls to DB
			//appUser = Optional.of(userAuthService.findByUserName(email));

			auditLogRouter.performLogsOperation(email, AuditEventType.JWT_VALIDATION_ATTEMPT,"Validating JWT for  "+ email);

			//Step 5 : Check if Token is BlackListed
			jwtService.validateNotBlacklisted(token);

			//Step 6 : Set The Authentication in Spring Context
			springauthSetter.setAuthentication(email, authorities);

			auditLogRouter.performLogsOperation(email, AuditEventType.JWT_VALIDATION_SUCCESS,"JWT validation succeeded for  "+ email);


		} catch (Exception ex) {

			auditLogRouter.performLogsOperation(email, AuditEventType.JWT_VALIDATION_FAILURE,ex.getMessage());
			throw new AuthorizationDeniedException(ex.getMessage());
		}

		// Continue with the remaining filter chain
		filterChain.doFilter(request, response);
	}


	/**
	 * This method decides whether this filter should be applied to the current request.
	 *
	 * <p>
	 * <strong>Logic:</strong> Skip this filter for the <code>PathExclusionMatcher</code> endpoint,
	 * since that’s where we generate the token — applying validation there would interfere.
	 * </p>
	 *
	 * <pre>
	 * Example:
	 * - Request to endpoint  → return true  → filter is skipped 
	 * - Request to endpoint  → return false → filter runs 
	 * </pre>
	 *
	 * @param request the current HTTP request
	 * @return true if the filter should NOT apply to this request
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

		return  PathExclusionMatcher.shouldExclude(request.getRequestURI());
	}

	public boolean isCurrentUriEqual(String currentUri , String pattern) {
		return currentUri.equals(pattern);
	}

}
