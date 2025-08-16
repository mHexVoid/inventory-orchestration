package com.hexvoid.inv.orch.jwt.constants;

public final class LogoutAttributes {
	private LogoutAttributes() {
	}
	public static final String STATUS = "LOGOUT_STATUS";   // "SUCCESS" | "FAIL"
	public static final String ERROR  = "LOGOUT_ERROR";    // reason string
}