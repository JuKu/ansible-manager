package com.jukusoft.anman.base.security.provider;

import com.jukusoft.anman.base.security.AuthProvider;
import com.jukusoft.anman.base.security.ExtendedAccountDTO;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/*
 * a ldap authentication provider to authenticate against a LDAP server.
 *
 * @author Justin Kuenzel
 */
@Service
public class LDAPAuthProvider implements AuthProvider {

	@Override
	public String getName() {
		return null;
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
