package com.hexvoid.inv.orch.logs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexvoid.inv.orch.logs.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog,Integer> {
	
}
