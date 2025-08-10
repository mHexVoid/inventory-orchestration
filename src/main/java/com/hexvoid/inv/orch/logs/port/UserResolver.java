package com.hexvoid.inv.orch.logs.port;

import com.hexvoid.inv.orch.auth.entity.AppUser;

public interface UserResolver {
	AppUser resolve (String username);
}
