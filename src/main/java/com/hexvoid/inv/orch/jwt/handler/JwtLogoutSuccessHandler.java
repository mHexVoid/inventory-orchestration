package com.hexvoid.inv.orch.jwt.handler;


import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hexvoid.inv.orch.jwt.constants.LogoutAttributes;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

	private final ObjectMapper om = new ObjectMapper();

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		try {

			if("FAIL".equals(request.getAttribute(LogoutAttributes.STATUS))) {

				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);

				om.writeValue(response.getOutputStream(),
						Map.of("message","logout_failed",
								"error", request.getAttribute(LogoutAttributes.ERROR)));
			}

			response.setStatus(HttpStatus.OK.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			om.writeValue(response.getOutputStream(),
					Map.of("message","logged_out"));

		}
		catch(Exception x) {

		}
	}
}
