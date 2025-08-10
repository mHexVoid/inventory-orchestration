package com.hexvoid.inv.orch.security.filter;

import java.io.IOException;

import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hexvoid.inv.orch.logs.context.AuditContext;
import com.hexvoid.inv.orch.logs.context.AuditContextProvider;
import com.hexvoid.inv.orch.logs.entity.AuditEventType;
import com.hexvoid.inv.orch.logs.service.AuditLogRouter;
import com.hexvoid.inv.orch.logs.service.AuditLogger;
import com.hexvoid.inv.orch.security.jwt.JwtAuthenticationSetter;
import com.hexvoid.inv.orch.security.jwt.JwtHeaderExtractor;
import com.hexvoid.inv.orch.security.jwt.JwtService;
import com.hexvoid.inv.orch.security.jwt.JwtTokenBlacklistChecker;
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
	private final JwtHeaderExtractor headerExtractor;
	private final JwtTokenBlacklistChecker blacklistChecker;
	private final JwtAuthenticationSetter authSetter;
	private final AuditLogger auditLogger;
	private final AuditContextProvider auditContextProvider;
	private final AuditLogRouter auditLogRouter;


	public JWTTokenValidatorFilter(JwtTokenBlacklistChecker blacklistChecker,JwtService jwtService, 
			JwtHeaderExtractor headerExtractor,JwtAuthenticationSetter authSetter,
			AuditLogger auditLogger,AuditContextProvider auditContextProvider,AuditLogRouter auditLogRouter) {
		this.jwtService = jwtService;
		this.headerExtractor = headerExtractor;
		this.blacklistChecker = blacklistChecker;
		this.authSetter = authSetter;
		this.auditLogger = auditLogger;
		this.auditContextProvider=auditContextProvider;
		this.auditLogRouter=auditLogRouter;
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
		String currentUri = auditContextProvider.getContext().getUri();
		boolean compareUriState = isCurrentUriEqual(currentUri,"/api/auth/logout");

		try {
			// Step 1: Extract the JWT token from the Authorization header
			String token = headerExtractor.extract(request);

			//Step 2: Retrieve secret key from environment or fallback default
			// && Parse and validate the JWT token
			Claims claims = jwtService.parseClaims(token);

			//Step 3 : Extract user information and authorities from the token 
			email = String.valueOf(claims.get("email"));
			String authorities = String.valueOf(claims.get("authorities"));

			auditLogRouter.performLogsOperation(email, AuditEventType.JWT_LOGOUT_ATTEMPT,"Logout attempt for User: : "+ email);

			if(compareUriState) {
				auditLogger.log(email, 
						AuditEventType.JWT_LOGOUT_ATTEMPT,
						"Logout attempt for User: : "+ email);
			}

			//Step 4 : Find The UserName for Logging Purpose
			//appUser = Optional.of(userAuthService.findByUserName(email));

			auditLogRouter.performLogsOperation(email, AuditEventType.JWT_VALIDATION_ATTEMPT,"Validating JWT for  "+ email);


			auditLogger.log(email, AuditEventType.JWT_VALIDATION_ATTEMPT,
					"Validating JWT for " + email);

			//Step 6 : Check if Token is BlackListed
			blacklistChecker.check(token);

			//Step 7 : Set The Authentication in Spring Context
			authSetter.setAuthentication(email, authorities);

			auditLogRouter.performLogsOperation(email, AuditEventType.JWT_VALIDATION_SUCCESS,"JWT validation succeeded for  "+ email);


			auditLogger.log(email, AuditEventType.JWT_VALIDATION_SUCCESS,
					"JWT validation succeeded for " +email);


			if(compareUriState) {
				auditLogger.log(email, 
						AuditEventType.JWT_LOGOUT_SUCCESS,
						"Logout successfull for User: : "+ email);
			}

			auditLogRouter.performLogsOperation(email, AuditEventType.JWT_LOGOUT_SUCCESS,"Logout successfull for User:  "+ email);


		} catch (Exception ex) {

			AuditEventType type = compareUriState ?
					AuditEventType.JWT_LOGOUT_FAILURE 
					: AuditEventType.JWT_VALIDATION_FAILURE;

			auditLogRouter.performLogsOperation(email, AuditEventType.JWT_LOGOUT_FAILURE,ex.getMessage());
			auditLogRouter.performLogsOperation(email, AuditEventType.JWT_VALIDATION_FAILURE,ex.getMessage());


			auditLogger.log(email, type, ex.getMessage());

			throw new AuthorizationDeniedException(ex.getMessage());
		}

		// Continue with the remaining filter chain
		filterChain.doFilter(request, response);
	}

	/**
	 * This method decides whether this filter should be applied to the current request.
	 *
	 * <p>
	 * <strong>Logic:</strong> Skip this filter for the <code>/user/details</code> endpoint,
	 * since that’s where we generate the token — applying validation there would interfere.
	 * </p>
	 *
	 * <pre>
	 * Example:
	 * - Request to /user/details  → return true  → filter is skipped ❌
	 * - Request to /leave/apply   → return false → filter runs ✅
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
