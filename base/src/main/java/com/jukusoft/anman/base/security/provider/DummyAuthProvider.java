package com.jukusoft.anman.base.security.provider;

import com.jukusoft.anman.base.security.AuthProvider;
import com.jukusoft.anman.base.security.ExtendedAccountDTO;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/*
 * a dummy authentication provider for junit tests.
 *
 * @author Justin Kuenzel
 */
public class DummyAuthProvider implements AuthProvider {

	@Override
	public String getName() {
		return "dummy-auth-provider";
	}

	@Override
	public Optional<ExtendedAccountDTO> login(String username, String password) {
		if (username.equals("test") && password.equals("test1234")) {
            return Optional.of(new ExtendedAccountDTO(2, username, username, username, new HashSet<String>()));
        }

		return Optional.empty();
	}

	@Override
	public Collection<String> listRoles(String username) {
		return new HashSet<>();
	}

	@Override
	public boolean hasRole(String username, String role) {
		return true;
	}

}
