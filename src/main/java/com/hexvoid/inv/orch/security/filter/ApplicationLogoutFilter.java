package com.hexvoid.inv.orch.security.filter;

import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


/*
 * As of now no Custom implementation has been done for this filter 
 * whereas LogoutFilter is internally getting used with LogoutHandler and LogoutSucessHandler
 * and been configured under SecurityFilterChain
 */
public class ApplicationLogoutFilter extends LogoutFilter {

	/**
	 * Constructor which takes a <tt>LogoutSuccessHandler</tt> instance to determine the
	 * target destination after logging out. The list of <tt>LogoutHandler</tt>s are
	 * intended to perform the actual logout functionality (such as clearing the security
	 * context, invalidating the session, etc.).
	 *
	 * @param logoutSuccessHandler
	 * @param handlers
	 */
	public ApplicationLogoutFilter(LogoutSuccessHandler logoutSuccessHandler, LogoutHandler[] handlers) {
		super(logoutSuccessHandler, handlers);
		// TODO Auto-generated constructor stub
	}

}
