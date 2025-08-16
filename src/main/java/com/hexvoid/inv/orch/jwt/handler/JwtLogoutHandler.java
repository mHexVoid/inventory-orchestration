package com.hexvoid.inv.orch.jwt.handler;

import java.time.Duration;
import java.time.Instant;

import javax.crypto.SecretKey;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.hexvoid.inv.orch.exception.UnauthorizedException;
import com.hexvoid.inv.orch.jwt.constants.ApplicationConstants;
import com.hexvoid.inv.orch.jwt.constants.LogoutAttributes;
import com.hexvoid.inv.orch.jwt.service.JwtService;
import com.hexvoid.inv.orch.jwt.service.JwtTokenBlacklistService;
import com.hexvoid.inv.orch.logs.entity.AuditEventType;
import com.hexvoid.inv.orch.logs.service.AuditLogRouter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtLogoutHandler implements LogoutHandler{

	private final JwtService jwtService;
	private final JwtTokenBlacklistService blacklistService;
	private final AuditLogRouter auditLogRouter;

	public JwtLogoutHandler(JwtService jwtService, JwtTokenBlacklistService blacklistService,
			AuditLogRouter auditLogRouter) {
		this.jwtService = jwtService;
		this.blacklistService = blacklistService;
		this.auditLogRouter = auditLogRouter;
	}



	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		// TODO Auto-generated method stub
		String header = request.getHeader(ApplicationConstants.JWT_HEADER_NAME);
		String email = null;

		try {

			String token = jwtService.extractToken(header);
			SecretKey SigningKey = jwtService.getSigningKey();
			Claims claims = jwtService.parseAndValidate(token, SigningKey);
			email = jwtService.getEmail(claims);
			//String jti = jwtService.getJti(claims);

			auditLogRouter.performLogsOperation(email,
					AuditEventType.JWT_LOGOUT_ATTEMPT, 
					"Logout initiated For user:"+email
					);

			Duration remaining = Duration.between(Instant.now(), jwtService.getExpiry(claims));

			if(remaining.isNegative() && remaining.isZero()) {
				throw new UnauthorizedException("User: "+email+" Token Already Expired Nothing to Revoke");
			}

			blacklistService.blacklistToken(token);

			auditLogRouter.performLogsOperation(email,
					AuditEventType.JWT_LOGOUT_SUCCESS, 
					"User: "+email+" Logged out Successhuly with Token BlackListed"
					);

			request.setAttribute(LogoutAttributes.STATUS, "SUCCESS");

		}
		catch(Exception x) {

			auditLogRouter.performLogsOperation(email,
					AuditEventType.JWT_LOGOUT_FAILURE, 
					"Logout failed: "+x.getMessage()
					);
			request.setAttribute(LogoutAttributes.STATUS, "FAIL");
			request.setAttribute(LogoutAttributes.ERROR, x.getMessage());
		}

	}

}
