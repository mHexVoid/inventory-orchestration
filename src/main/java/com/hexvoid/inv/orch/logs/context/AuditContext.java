package com.hexvoid.inv.orch.logs.context;

public class AuditContext  {

	private final String uri;
	private final String correlationId;

	public AuditContext(String uri , String correlationId){
		this.uri=uri;
		this.correlationId=correlationId;
	}

	public String getUri() {
		return uri;
	}

	public String getCorrelationId() {
		return correlationId;
	}

}
