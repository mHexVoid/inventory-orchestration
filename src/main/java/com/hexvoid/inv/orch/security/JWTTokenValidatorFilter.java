//package com.hexvoid.inv.orch.security;
//
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.Optional;
//
//import javax.crypto.SecretKey;
//
//import org.springframework.core.env.Environment;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import com.hexvoid.inv.orch.auth.entity.AppUser;
//import com.hexvoid.inv.orch.auth.service.UserAuthService;
//import com.hexvoid.inv.orch.constants.ApplicationConstants;
//import com.hexvoid.inv.orch.logs.entity.AuditEventType;
//import com.hexvoid.inv.orch.logs.service.AuditLogService;
//import com.hexvoid.inv.orch.security.service.JwtTokenBlacklistService;
//import com.hexvoid.inv.orch.security.util.PathExclusionMatcher;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
///**
// * This filter is responsible for validating incoming JWT tokens from client requests.
// *
// * <p>
// * It is applied to all endpoints <strong>except</strong> <code>/user/details</code> (where the token is generated).
// * <br>
// * This filter:
// * <ul>
// *     <li>Extracts the token from the <code>Authorization</code> header</li>
// *     <li>Parses and validates the token using the secret key</li>
// *     <li>Extracts user details and sets them in Spring Security’s context</li>
// * </ul>
// * Once the context is set, Spring Security handles authorization based on roles/authorities.
// * </p>
// */
//public class JWTTokenValidatorFilter extends OncePerRequestFilter {
//	/**
//	 * @param request
//	 * @param response
//	 * @param filterChain
//	 * @throws ServletException
//	 * @throws IOException
//	 */
//
//	private final Environment environment;
//	private final JwtTokenBlacklistService tokenBlacklistService;
//	private final AuditLogService auditLogService;
//	private final UserAuthService userAuthService;
//
//
//	public JWTTokenValidatorFilter(Environment environment, JwtTokenBlacklistService tokenBlacklistService,
//			AuditLogService auditLogService, UserAuthService userAuthService 
//			) {
//		this.environment = environment;
//		this.tokenBlacklistService = tokenBlacklistService;
//		this.auditLogService=auditLogService;
//		this.userAuthService=userAuthService;
//	}
//
//	/**
//	 * Intercepts every request before controller execution to:
//	 * <ul>
//	 *     <li>Validate the JWT token and parse (if present in the request header.)</li>
//	 *     <li>Extract user data and set authentication in SecurityContext</li>
//	 * </ul>
//	 *
//	 * @param request     the incoming HTTP request from the client
//	 * @param response    the outgoing HTTP response
//	 * @param filterChain filter chain to pass request/response to the next filter
//	 * @throws ServletException if a servlet-specific error occurs
//	 * @throws IOException      if an input/output error occurs
//	 */
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,
//			FilterChain filterChain) throws ServletException, IOException {
//
//
//		Optional<AppUser> appUser = Optional.empty() ;
//
//		// Step 1: Extract the JWT token from the Authorization header
//		String authHeader = extractAuthHeader(request) ;
//
//
//		try {
//
//			// Step 2: Retrieve secret key from environment or fallback default
//			SecretKey secretKey = getSecretKey();
//
//			if (secretKey == null) {
//				throw new IllegalStateException("JWT Secret key is not configured.");
//			}
//			// Step 3: Parse and validate the JWT token
//			Claims claims = parseJWTClaims(secretKey , authHeader);
//
//			// Step 4: Extract user information and authorities from the token
//			String username = String.valueOf(claims.get("email"));
//			String authorities = String.valueOf(claims.get("authorities"));
//
//			// 5. Fetch user details
//			appUser = Optional.of(userAuthService.findByUserName(username));
//
//			auditLogService.logEvent(
//					appUser.orElse(null),  AuditEventType.JWT_VALIDATION_ATTEMPT,
//					"Attempting to Validate JWT for User : "+appUser.map(AppUser::getUsername).orElse("UNKNOWN")
//					);
//
//			// 6. Check token blacklist
//			checkIfBlacklisted(authHeader , appUser );
//
//
//			// Step 7: Set the user authentication in the SecurityContext for Spring Security
//			setAuthentication( username , authorities);
//
//
//			auditLogService.logEvent(
//					appUser.orElse(null),  AuditEventType.JWT_VALIDATION_SUCCESS,
//					"JWT Validation Successfull for User :"+appUser.map(AppUser::getUsername).orElse("UNKNOWN")
//
//					);
//
//		} catch (Exception e) {
//
//			auditLogService.logEvent(
//					appUser.orElse(null),  AuditEventType.JWT_VALIDATION_FAILURE,
//					"Invalid JWT Token Received :"+appUser.map(AppUser::getUsername).orElse("UNKNOWN")
//					);
//
//			// Token is invalid, expired, tampered or malformed
//			throw new BadCredentialsException("Invalid JWT Token Received",e);
//		}
//
//		// Continue with the remaining filter chain
//		filterChain.doFilter(request, response);
//	}
//
//	private void checkIfBlacklisted(String token, Optional<AppUser> appUser) {
//
//		if (tokenBlacklistService.isTokenBlacklisted(token)) {
//
//			auditLogService.logEvent(
//					appUser.orElse(null),  AuditEventType.JWT_EXPIRED,
//					"JWT Token is BlackListed"
//					);
//
//			throw new BadCredentialsException("JWT Token is blacklisted. Please login again.");
//		}
//	}
//
//
//	private void setAuthentication(String username, String authorities) {
//
//		Authentication authentication = new UsernamePasswordAuthenticationToken(
//				username,
//				null,
//				AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
//				);
//
//		SecurityContextHolder.getContext().setAuthentication(authentication);
//
//	}
//
//	private Claims parseJWTClaims(SecretKey secretKey, String authHeader) {
//
//		Claims claims = Jwts.parser()
//				.verifyWith(secretKey)
//				.build()
//				.parseSignedClaims(authHeader)
//				.getPayload();
//
//		return claims;
//	}
//
//	private SecretKey getSecretKey() {
//
//		SecretKey secretKey = null;
//
//		if (environment != null) {
//
//			String secretKeyEnv = environment.getProperty(
//					ApplicationConstants.JWT_SECRET_KEY,
//					ApplicationConstants.JWT_DEFAULT_SECRET_VALUE
//					);
//
//			secretKey = Keys.hmacShaKeyFor(secretKeyEnv.getBytes(StandardCharsets.UTF_8));
//		}
//		return secretKey;
//	}
//
//	private String extractAuthHeader(HttpServletRequest request) {
//
//		String path = request.getRequestURI();
//		String authHeader = request.getHeader(ApplicationConstants.JWT_HEADER_NAME);
//
//		if(authHeader==null ) {
//
//			AuditEventType eventType = path.equals("/api/auth/logout")
//					? AuditEventType.JWT_LOGOUT_FAILURE
//							: AuditEventType.JWT_VALIDATION_FAILURE;
//
//			String message =  path.equals("/api/auth/logout") 
//					? "Logout attempt failed due to missing token" 
//							: "JWT Validation failed due to missing token";
//
//			auditLogService.logEvent( null,  eventType, message );
//
//			throw new BadCredentialsException("JWT Token Missing in request header.");
//		}
//
//		return authHeader;
//
//	}
//
//	/**
//	 * This method decides whether this filter should be applied to the current request.
//	 *
//	 * <p>
//	 * <strong>Logic:</strong> Skip this filter for the <code>/user/details</code> endpoint,
//	 * since that’s where we generate the token — applying validation there would interfere.
//	 * </p>
//	 *
//	 * <pre>
//	 * Example:
//	 * - Request to /user/details  → return true  → filter is skipped ❌
//	 * - Request to /leave/apply   → return false → filter runs ✅
//	 * </pre>
//	 *
//	 * @param request the current HTTP request
//	 * @return true if the filter should NOT apply to this request
//	 */
//	@Override
//	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//
//		return PathExclusionMatcher.shouldExclude(request.getRequestURI());
//
//
//
//	}
//}