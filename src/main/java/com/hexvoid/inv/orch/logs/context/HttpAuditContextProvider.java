package com.hexvoid.inv.orch.logs.context;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class HttpAuditContextProvider implements AuditContextProvider {


	private final HttpServletRequest request;

	HttpAuditContextProvider(HttpServletRequest request){
		this.request=request;
	}

	@Override
	public AuditContext getContext() {

		String uri = request.getRequestURI();
		String correlationId = (String) request.getAttribute("correlationId");
		return new AuditContext(uri, correlationId);
	}

}
