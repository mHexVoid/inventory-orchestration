package com.hexvoid.inv.orch.security.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.hexvoid.inv.orch.logs.entity.AuditEventType;
import com.hexvoid.inv.orch.logs.service.AuditLogger;
import com.hexvoid.inv.orch.security.util.ApplicationAuthoritiesContext;

@Component
public class JwtAuthenticationSetter  {

	public final ApplicationAuthoritiesContext applicationAuthoritiesContext;
	public final AuditLogger auditLogger;

	public JwtAuthenticationSetter(ApplicationAuthoritiesContext applicationAuthoritiesContext ,AuditLogger auditLogger ) {
		this.applicationAuthoritiesContext=applicationAuthoritiesContext;
		this.auditLogger=auditLogger;
	}

	public void setAuthentication(String username, String authorities) {

		Authentication auth = new UsernamePasswordAuthenticationToken(
				username,
				null,
				AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
				);

		boolean authoritiesMatching = applicationAuthoritiesContext.isAuthoritiesMatching(authorities);

		if(authoritiesMatching) {
			auditLogger.log(username,
					AuditEventType.JWT_VALIDATION_ATTEMPT,
					"User: " + username + " Authenticated Successfully"
					);
		}
		else {
			throw new AuthorizationDeniedException("User " + username + " does not have sufficient authorities to access the resource");
		}

		SecurityContextHolder.getContext().setAuthentication(auth);			
	}
}