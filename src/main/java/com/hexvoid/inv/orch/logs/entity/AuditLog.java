package com.hexvoid.inv.orch.logs.entity;
import java.time.LocalDateTime;

import com.hexvoid.inv.orch.auth.entity.AppUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="logs")
public class AuditLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;

	@Column(name="timestamp")
	private LocalDateTime timestamp;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private AppUser user;

	@Enumerated(EnumType.STRING)
	@Column(name="event_type")
	private AuditEventType  eventType;

	@Column(name="endpoint")
	private String endPoint;

	@Column(name="details")
	private String details;

	@Column(name="correlation_id")
	private String correlationId;

	public AppUser getUser() {
		return user;
	}

	public void setUser(AppUser user) {
		this.user = user;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public AuditEventType getEventType() {
		return eventType;
	}

	public void setEventType(AuditEventType eventType) {
		this.eventType = eventType;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
}
