package com.jukusoft.anman.base.config;

import org.springframework.ldap.core.AuthenticationSource;

/*
 * an adapter class for authentication source.
 *
 * @author Justin Kuenzel
 */
public class AuthenticationSourceAdapter implements AuthenticationSource {

	/**
	 * the principal cn.
	 */
	private final String principal;

	/**
	 * the password.
	 */
	private final String password;

	/**
	 * constructor.
	 *
	 * @param principal the principal
	 * @param password the password
	 */
	public AuthenticationSourceAdapter(String principal, String password) {
		this.principal = principal;
		this.password = password;
	}

	@Override
	public String getPrincipal() {
		return principal;
	}

	@Override
	public String getCredentials() {
		return password;
	}

}
