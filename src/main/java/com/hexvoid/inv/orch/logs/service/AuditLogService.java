package com.hexvoid.inv.orch.logs.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hexvoid.inv.orch.auth.entity.AppUser;
import com.hexvoid.inv.orch.logs.context.AuditContext;
import com.hexvoid.inv.orch.logs.context.AuditContextProvider;
import com.hexvoid.inv.orch.logs.dto.AppLogs;
import com.hexvoid.inv.orch.logs.entity.AuditLog;
import com.hexvoid.inv.orch.logs.port.UserResolver;
import com.hexvoid.inv.orch.logs.repository.AuditLogRepository;

@Service
public class AuditLogService {

	private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

	private final AuditLogRepository auditLogRepository;
	private final AuditContextProvider auditContextProvider;
	private final UserResolver userResolver;

	public AuditLogService(AuditLogRepository auditLogRepository,
			AuditContextProvider auditContextProvider,UserResolver userResolver) {
		
		this.auditLogRepository = auditLogRepository;
		this.auditContextProvider = auditContextProvider;
		this.userResolver = userResolver;
	}


	public void logEvent(AppLogs logs) {
		
		AuditContext ctx = auditContextProvider.getContext();

		String username = logs.getUsername();
		Optional<AppUser> appUser = Optional.ofNullable(userResolver.resolve(username));

		logger.info("[AUDIT] [{}] [{}] [{}] - {}", ctx.getCorrelationId(), logs.getEventType(), username, logs.getMessage());

		AuditLog log = new AuditLog();
		log.setTimestamp(LocalDateTime.now());
		log.setUser(appUser.orElse(null));
		log.setEventType(logs.getEventType());
		log.setEndPoint(ctx.getUri());
		log.setDetails(logs.getMessage());
		log.setCorrelationId(ctx.getCorrelationId());

		auditLogRepository.save(log);
	}
}
