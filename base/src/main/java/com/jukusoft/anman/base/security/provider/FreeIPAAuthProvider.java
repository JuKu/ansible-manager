package com.jukusoft.anman.base.security.provider;

import com.jukusoft.anman.base.security.AuthProvider;
import com.jukusoft.anman.base.security.ExtendedAccountDTO;

import java.util.Collection;
import java.util.Optional;

/*
 *
 *
 * @author Justin Kuenzel
 */
public class FreeIPAAuthProvider implements AuthProvider {

	@Override
	public String getName() {
		return "freeipa-client";
	}

	@Override
	public Optional<ExtendedAccountDTO> login(String username, String password) {
		return Optional.empty();
	}

	@Override
	public Collection<String> listRoles(String username) {
		return null;
	}

	@Override
	public boolean hasRole(String username, String role) {
		return false;
	}

}
