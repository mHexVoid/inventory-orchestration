package com.hexvoid.inv.orch.logs.adapter;

import org.springframework.stereotype.Component;

import com.hexvoid.inv.orch.auth.entity.AppUser;
import com.hexvoid.inv.orch.auth.repository.UserRepository;
import com.hexvoid.inv.orch.logs.port.UserResolver;

@Component
public class UserResolverImpl implements UserResolver {

	private final UserRepository userRepository;

	public UserResolverImpl(UserRepository userRepository){
		this.userRepository=userRepository;
	}

	@Override
	public AppUser resolve(String username) {
		return userRepository.findByUsername(username);
	}
	

}
