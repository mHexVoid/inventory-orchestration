package com.hexvoid.inv.orch.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexvoid.inv.orch.auth.entity.AppUser;

public interface UserRepository extends JpaRepository<AppUser,Integer> {

	//@Query("SELECT u FROM User u WHERE u.username = :username")
	AppUser findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
}
