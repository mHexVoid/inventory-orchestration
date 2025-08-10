package com.hexvoid.inv.orch.security.jwt;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import com.hexvoid.inv.orch.constants.ApplicationConstants;
import com.hexvoid.inv.orch.logs.context.AuditContext;
import com.hexvoid.inv.orch.logs.context.AuditContextProvider;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtHeaderExtractor {

	private final AuditContextProvider auditContextProvider;

	public JwtHeaderExtractor( AuditContextProvider auditContextProvider ) {
		this.auditContextProvider=auditContextProvider;
	}

	public String extract(HttpServletRequest request) {

		String token = request.getHeader(ApplicationConstants.JWT_HEADER_NAME);

		AuditContext context = auditContextProvider.getContext();
		if (token == null) {

			String msg = context.getUri().equals("/api/auth/logout") ?
					"Logout failed: Missing token" : "Validation failed: Missing token";

			throw new BadCredentialsException(msg);
		}

		return token;
	}
}