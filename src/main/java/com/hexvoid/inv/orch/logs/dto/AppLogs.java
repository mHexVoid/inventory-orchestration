package com.hexvoid.inv.orch.logs.dto;

import com.hexvoid.inv.orch.logs.entity.AuditEventType;


public class AppLogs {

	private String username;
	private AuditEventType eventType;
	private String message;

	public static AppLogs of(String username, AuditEventType eventType, String message) {
		return new AppLogs(username, eventType, message);
	}

	public AppLogs(String username, AuditEventType eventType, String message) {
		this.username = username;
		this.eventType = eventType;
		this.message = message;
	}

	public String getUsername() {
		return username;
	}

	public AuditEventType getEventType() {
		return eventType;
	}

	public String getMessage() {
		return message;
	}

}
