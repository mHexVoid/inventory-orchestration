package com.hexvoid.inv.orch.logs.service;

import org.springframework.stereotype.Component;

import com.hexvoid.inv.orch.logs.dto.AppLogs;
import com.hexvoid.inv.orch.logs.entity.AuditEventType;

@Component
public class AuditLogger {
	
	private final AuditLogService auditLogService;

	public AuditLogger(AuditLogService auditLogService) {
		this.auditLogService = auditLogService;
	}

	public void log(String username, AuditEventType eventType, String message) {
		auditLogService.logEvent(AppLogs.of(username, eventType, message));
	}
	
	
}