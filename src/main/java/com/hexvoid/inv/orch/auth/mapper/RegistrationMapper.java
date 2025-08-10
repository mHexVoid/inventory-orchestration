package com.hexvoid.inv.orch.auth.mapper;

import com.hexvoid.inv.orch.auth.dto.AppUserDto;
import com.hexvoid.inv.orch.auth.entity.AppUser;


public class RegistrationMapper {

	private RegistrationMapper() {
	}

	public static AppUser toDAO(AppUserDto appUserDto) {

		AppUser obj = new AppUser();
		obj.setEmail(appUserDto.getEmail());
		obj.setPassword(appUserDto.getPassword());
		obj.setUsername(appUserDto.getUsername());

		return obj;
	}

}
