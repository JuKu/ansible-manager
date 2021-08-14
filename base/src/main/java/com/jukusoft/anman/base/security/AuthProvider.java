package com.jukusoft.anman.base.security;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

/*
 * this interface is for auth provider plugins which can extend the application with new authenticaton mechanism like LDAP.
 *
 * @author Justin Kuenzel
 */
@Component
public interface AuthProvider extends Comparable<AuthProvider> {

	/**
	 * get name of authentication provider.
	 *
	 * @return name of authentication provider
	 */
	public String getName();

	/**
	 * get the priority of the authentication provider.
	 * Lesser is better (1 - execute first, 10 - execute last).
	 *
	 * @return priority of authentication provider
	 */
	public default int getPriority() {
		return 5;
	}

	/**
	 * try to login user.
	 *
	 * @param username username
	 * @param password password
	 *
	 * @return account object if credentials are correct, else empty object
	 */
	public Optional<ExtendedAccountDTO> login(String username, String password);

	/**
	 * list all roles of user.
	 *
	 * @return all roles of user
	 */
	public Collection<String> listRoles(String username);

	/**
	 * check, if the user has a specific role.
	 *
	 * @param username username
	 * @param role     requested role
	 *
	 * @return true, if the user has this role
	 */
	public boolean hasRole(String username, String role);

	/**
	 * compare to other objects.
	 *
	 * @param o other object
	 *
	 * @return -1, 0 or 1
	 */
	@Override
	default int compareTo(AuthProvider o) {
		return Integer.compare(this.getPriority(), o.getPriority());
	}

}
