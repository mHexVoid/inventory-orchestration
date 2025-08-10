package com.hexvoid.inv.orch.auth.service;

import com.hexvoid.inv.orch.auth.entity.AppUser;


public interface UserAuthService {
	
    AppUser findByUserName(String name);
    AppUser save(AppUser userCreds);
}
