package com.hexvoid.inv.orch.security.util;


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.hexvoid.inv.orch.logs.context.AuditContext;
import com.hexvoid.inv.orch.logs.context.AuditContextProvider;

@Component
public final class ApplicationAuthoritiesContext  {


	private final Logger logger = LoggerFactory.getLogger(ApplicationAuthoritiesContext.class);

	private final AuditContextProvider auditContextProvider;
	private final Map<String, List<String>> roleBasedApiPaths = SecurityPathRegistry.ROLE_BASED_API_PATHS;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	ApplicationAuthoritiesContext(AuditContextProvider auditContextProvider){
		this.auditContextProvider=auditContextProvider;
	}

	public  boolean isAuthoritiesMatching(String springSecurityRole) {

		AuditContext context = auditContextProvider.getContext();
		String requestUri = context.getUri();
		String actualRole = normalizeSpringSecurityRole(springSecurityRole);
		//List<String> allowedRoles = roleBasedApiPaths.get(requestUri);

		logger.debug("Incoming Request => URI: {} | Provided Role: {} | Normalized Role: {}",
				requestUri, springSecurityRole, actualRole);

		String printfPattern ="| %-30s | %-30s |%n";
		String rowBorderCxt = "+--------------------------------+--------------------------------+";
		System.out.println(rowBorderCxt);
		System.out.printf(printfPattern,"Field","Value");
		System.out.println(rowBorderCxt);
		System.out.printf(printfPattern,"Incoming Request URI",requestUri);
		System.out.printf(printfPattern,"Provided Role",springSecurityRole);
		System.out.printf(printfPattern,"Normalized Role",actualRole);

		for(Entry<String, List<String>> path : roleBasedApiPaths.entrySet()) {
			String patternKey = path.getKey();
			List<String> allowedRoles = path.getValue();

			if(pathMatcher.match(patternKey, requestUri)) {

				System.out.println(rowBorderCxt);
				System.out.printf(printfPattern,"Matched pattern",patternKey);
				System.out.printf(printfPattern,"Allowed roles",allowedRoles);
				System.out.println(rowBorderCxt);
				logger.debug("Matched Pattern: {} | Allowed Roles: {}", patternKey, allowedRoles);

				return allowedRoles.contains(actualRole);
			}
		}
		//		if (allowedRoles != null) {
		//			System.out.println("Allowed Roles for URI: " + allowedRoles);
		//			return allowedRoles.contains(actualRole);
		//		}
		System.out.println(rowBorderCxt);
		System.out.printf(printfPattern,"No Matching Pattern found for",requestUri);
		System.out.printf(printfPattern,"Default Action Applied ",true);
		System.out.println(rowBorderCxt);

		logger.debug("No matching pattern found for URI: {} . | Having Role {} Default allow applied.", requestUri, actualRole);

		return true; // Consider denying by default in stricter environments
	}

	private String normalizeSpringSecurityRole(String role) {
		if(role.startsWith("ROLE_")) {
			return role.replace("ROLE_","");
		}
		return role;
		//return (role != null && role.startsWith("ROLE_")) ? role.substring(5) : role;
	}
}
