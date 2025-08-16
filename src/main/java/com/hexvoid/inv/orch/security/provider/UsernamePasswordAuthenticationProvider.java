package com.hexvoid.inv.orch.security.provider;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hexvoid.inv.orch.exception.ResourceNotFoundException;
import com.hexvoid.inv.orch.logs.entity.AuditEventType;
import com.hexvoid.inv.orch.logs.service.AuditLogRouter;
import com.hexvoid.inv.orch.security.service.CustomUserDetailsService;

@Component
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

	private final CustomUserDetailsService customUserDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final AuditLogRouter auditLogRouter;


	public UsernamePasswordAuthenticationProvider(CustomUserDetailsService customUserDetailsService, 
			PasswordEncoder passwordEncoder,AuditLogRouter auditLogRouter ) {
		this.customUserDetailsService=customUserDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.auditLogRouter = auditLogRouter;
	}

	/**
	 * Performs authentication with the same contract as
	 * {@link AuthenticationManager#authenticate(Authentication)}
	 * .
	 *
	 * @param authentication the authentication request object.
	 * @return a fully authenticated object including credentials. May return
	 * <code>null</code> if the <code>AuthenticationProvider</code> is unable to support
	 * authentication of the passed <code>Authentication</code> object. In such a case,
	 * the next <code>AuthenticationProvider</code> that supports the presented
	 * <code>Authentication</code> class will be tried.
	 * @throws AuthenticationException if authentication fails.
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		try {

			UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

			boolean credentialsMatches = passwordEncoder.matches(password, userDetails.getPassword());

			auditLogRouter.performLogsOperation(username,
					AuditEventType.AUTH_ATTEMPT,
					"User " + username + " Password matches ? =  " + credentialsMatches
					);

			if (credentialsMatches) {
				auditLogRouter.performLogsOperation(username,
						AuditEventType.AUTH_SUCCESS,
						"User: " + username + " Authenticated Successfully"
						);

				return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
			} 

			throw new BadCredentialsException("Bad credentials");

		} catch (AuthenticationException e) {
			// Known spring exceptions like BadCredentials
			auditLogRouter.performLogsOperation(username,
					AuditEventType.AUTH_FAILURE,
					"Authentication failed: " + e.getMessage()
					);
			throw e;
		}
		catch (ResourceNotFoundException e){
			throw e;
		}
		catch (Exception ex) {
			// Catch unexpected errors (NPE, DB down, etc.)
			{
				auditLogRouter.performLogsOperation(username,
						AuditEventType.AUTH_FAILURE,
						"Authentication failed due to server error: " + ex.getClass().getSimpleName()
						);
			}
			throw ex;
		}
	}

	/**
	 * Returns <code>true</code> if this <Code>AuthenticationProvider</code> supports the
	 * indicated <Code>Authentication</code> object.
	 * <p>
	 * Returning <code>true</code> does not guarantee an
	 * <code>AuthenticationProvider</code> will be able to authenticate the presented
	 * <code>Authentication</code> object. It simply indicates it can support closer
	 * evaluation of it. An <code>AuthenticationProvider</code> can still return
	 * <code>null</code> from the {@link #authenticate(Authentication)} method to indicate
	 * another <code>AuthenticationProvider</code> should be tried.
	 * </p>
	 * <p>
	 * Selection of an <code>AuthenticationProvider</code> capable of performing
	 * authentication is conducted at runtime the <code>ProviderManager</code>.
	 * </p>
	 *
	 * @param authentication
	 * @return <code>true</code> if the implementation can more closely evaluate the
	 * <code>Authentication</code> class presented
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
