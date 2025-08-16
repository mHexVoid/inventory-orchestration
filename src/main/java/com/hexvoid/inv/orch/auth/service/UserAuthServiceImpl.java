package com.hexvoid.inv.orch.auth.service;

import org.springframework.stereotype.Service;

import com.hexvoid.inv.orch.auth.entity.AppUser;
import com.hexvoid.inv.orch.auth.entity.Roles;
import com.hexvoid.inv.orch.auth.repository.UserRepository;
import com.hexvoid.inv.orch.exception.ApiException;
import com.hexvoid.inv.orch.logs.entity.AuditEventType;
import com.hexvoid.inv.orch.logs.service.AuditLogRouter;

@Service
public class UserAuthServiceImpl implements UserAuthService {

	private final UserRepository userRepository;
	private final AuditLogRouter auditLogRouter;

	public UserAuthServiceImpl(UserRepository userRepository , AuditLogRouter auditLogRouter) {
		this.userRepository = userRepository;
		this.auditLogRouter = auditLogRouter;
	}

	@Override
	public AppUser save(AppUser userCreds) {

		validateDuplicateUser(userCreds);
		resolveRole(userCreds);

		auditLogRouter.performLogsOperation(
				null, // user not created yet
				AuditEventType.USER_REGISTER_ATTEMPT,
				"Attempting to save user: " + userCreds.getUsername()
				);

		AppUser savedUser = userRepository.save(userCreds);

		return savedUser;

	}

	/**
	 * @param name
	 * @return
	 */
	@Override
	public AppUser findByUserName(String name) {
		AppUser appUser = userRepository.findByUsername(name);
		return appUser;
	}


	private void validateDuplicateUser(AppUser user) throws ApiException {

		if (userRepository.existsByUsername(user.getUsername())) {

			auditLogRouter.performLogsOperation(
					null, // user not created yet
					AuditEventType.USER_REGISTER_FAILED,
					"Duplicate username found: " + user.getUsername()
					);

			throw new ApiException("Username already exists: " + user.getUsername());
		}

		if (userRepository.existsByEmail(user.getEmail())) {

			auditLogRouter.performLogsOperation(
					null, // user not created yet
					AuditEventType.USER_REGISTER_FAILED,
					"Duplicate email found: " + user.getEmail()
					);

			throw new ApiException("Email already exists: " + user.getEmail());
		}
	}

	private  void resolveRole(AppUser user) {

		if (user.getEmail().endsWith("@hexvoid.com") || user.getEmail().endsWith("@apix.com")) {

			user.setRoles(Roles.ADMIN);

			auditLogRouter.performLogsOperation(
					null, // user not created yet
					AuditEventType.USER_REGISTER_ATTEMPT,
					"User " + user.getUsername() + " assigned role ADMIN based on email domain"
					);

		} else {
			user.setRoles(Roles.USER);

			auditLogRouter.performLogsOperation(
					null, // user not created yet
					AuditEventType.USER_REGISTER_ATTEMPT,
					"User " + user.getUsername() + " assigned role USER based on email domain"
					);
		}
	}
}
