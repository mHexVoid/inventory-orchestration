package com.hexvoid.inv.orch.logs.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.hexvoid.inv.orch.logs.context.AuditContextProvider;
import com.hexvoid.inv.orch.logs.entity.AuditEventType;
import com.hexvoid.inv.orch.security.util.SecurityPathRegistry;

@Component
public class AuditLogRouter  {

	private final AuditLogger auditLogger;
	private final AuditContextProvider auditContextProvider;
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	private static String PATTERN ="| %-15s | %-30s | %-30s |%n";
	private static String TABLE_ROW="+-----------------+--------------------------------+--------------------------------+";


	List<String> authenticatedApiPaths = SecurityPathRegistry.AUTHENTICATED_API_PATHS;
	List<String> publicApiPaths = SecurityPathRegistry.PUBLIC_API_PATHS;
	Map<String, List<String>> roleBasedApiPaths = SecurityPathRegistry.ROLE_BASED_API_PATHS;

	public AuditLogRouter (AuditLogger auditLogger , AuditContextProvider auditContextProvider){
		this.auditLogger=auditLogger;
		this.auditContextProvider=auditContextProvider;
	}


	//name : Can be Any but set in context Half of issue will get resolved
	//AuditType : Can get it on the basis of context URI
	//Message : Once You Have Context URI Update Message Accordingly 
	//SecurityPathRegistry will contain each and every URI

	public void performLogsOperation(String name , AuditEventType eventType , String message ) {

		String incomingURI  = auditContextProvider.getContext().getUri();

		if(isPublicUri(incomingURI)) {
			handlePublicUri(incomingURI, name, eventType, message);
		}
		else if(isAuthenticatedUri(incomingURI)) {
			handleAuthenticatedUri(incomingURI, name, eventType, message);
		}
		else if(isRoleBasedUri(incomingURI)) {
			handleRoleBasedUri(incomingURI, name, eventType, message);
		}
	}

	private void handleRoleBasedUri(String uri, String name, AuditEventType type, String msg) {

		if(uriEquals(uri,"/api/products")) {

			logDefault(name, type, msg);

			auditLogger.log(name, type, msg);
		}

		logDefault(name, type, msg);

		auditLogger.log(name, type, msg);
	}

	private void handleAuthenticatedUri(String uri, String name, AuditEventType type, String msg) {
		if(uriEquals(uri,"/api/auth/logout")) {

			logDefault(name, type, msg);

			auditLogger.log(name, type, msg);
		}

	}

	private void handlePublicUri(String uri, String name, AuditEventType type, String msg) {
		if(uriEquals(uri,"/api/auth/login")) {

			logDefault(name, type, msg);

			auditLogger.log(name, type, msg);
		}
		else if(uriEquals(uri,"/api/auth/register")) {

			logDefault(name, type, msg);

			auditLogger.log(name, type, msg);
		}

	}

	private boolean isRoleBasedUri(String uri) {
		return roleBasedApiPaths.keySet().stream().anyMatch(path -> pathMatcher.match(path, uri));
	}

	private boolean isAuthenticatedUri(String uri) {
		return authenticatedApiPaths.stream().anyMatch(path -> pathMatcher.match(path, uri));
	}

	private boolean isPublicUri(String uri) {
		return publicApiPaths.stream().anyMatch(path -> pathMatcher.match(path, uri));
	}

	private boolean uriEquals(String incomingURI , String staticURI) {

		return incomingURI.equals(staticURI);
	}

	private void logDefault(String name, AuditEventType type, String msg) {

		System.out.println(TABLE_ROW);
		System.out.printf(PATTERN ,"User Name","Audit Event Type","Message");
		System.out.println(TABLE_ROW);
		System.out.printf(PATTERN ,name,type,msg);
		System.out.println(TABLE_ROW);

	}

}
