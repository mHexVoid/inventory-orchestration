package com.hexvoid.inv.orch.security.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hexvoid.inv.orch.auth.entity.AppUser;
import com.hexvoid.inv.orch.auth.service.UserAuthService;
import com.hexvoid.inv.orch.exception.ResourceNotFoundException;
import com.hexvoid.inv.orch.logs.entity.AuditEventType;
import com.hexvoid.inv.orch.logs.service.AuditLogRouter;

@Service
public class CustomUserDetailsService implements UserDetailsService{


	private final UserAuthService userAuthService;
	private final AuditLogRouter auditLogRouter;

	Optional<AppUser> user;

	public CustomUserDetailsService(UserAuthService userAuthService , AuditLogRouter auditLogRouter) {
		this.userAuthService = userAuthService;
		this.auditLogRouter = auditLogRouter;
	}

	/**
	 * Locates the user based on the username. In the actual implementation, the search
	 * may possibly be case sensitive, or case insensitive depending on how the
	 * implementation instance is configured. In this case, the <code>UserDetails</code>
	 * object that comes back may have a username that is of a different case than what
	 * was actually requested..
	 *
	 * @param username the username identifying the user whose data is required.
	 * @return a fully populated user record (never <code>null</code>)
	 * @throws UsernameNotFoundException if the user could not be found or the user has no
	 *                                   GrantedAuthority
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


		try{
			user = Optional.ofNullable(userAuthService.findByUserName(username));

			auditLogRouter.performLogsOperation(username,
					AuditEventType.AUTH_ATTEMPT,
					"Login attempt for user: " + username
					);

			if (user.isEmpty()) {
				throw new ResourceNotFoundException("User : " + username + " not found");
			}

		}
		catch(ResourceNotFoundException e){

			auditLogRouter.performLogsOperation(username,AuditEventType.AUTH_FAILURE,e.getMessage());

			throw e;

		}


		//List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" +user.map(AppUser::getRoles).map(Roles::name)));
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" +getAuthorities()));

		return new User(getUserName(), getPassword(), authorities);
	}


	private String getAuthorities() {
		return user.map(t->t.getRoles().name()).orElse("NULL");
	}

	private String getPassword() {
		return user.map(AppUser::getPassword).orElse("NULL");
	}

	private String getUserName() {
		return user.map(t->t.getEmail()).orElse("NULL");
	}

	//	public Optional<AppUser> getUser() {
	//		return user;
	//	}

}

