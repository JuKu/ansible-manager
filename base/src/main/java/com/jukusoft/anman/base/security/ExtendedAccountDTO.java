package com.jukusoft.anman.base.security;

import com.jukusoft.authentification.jwt.account.AccountDTO;

import java.util.Set;

/*
 * an extension for the AccountDTO to also store additional information like the prename and lastname.
 *
 * @author Justin Kuenzel
 */
public class ExtendedAccountDTO extends AccountDTO {

	/**
	 * the prename of the user.
	 */
	private String prename;

	/**
	 * the lastname of the user.
	 */
	private String lastname;

	/**
	 * list of permissions.
	 */
	private Set<String> roles;

	/**
	 * constructor.
	 *
	 * @param userID user id
	 * @param username username
	 */
	public ExtendedAccountDTO(long userID, String username, String prename, String lastname, Set<String> roles) {
		super(userID, username);
		this.prename = prename;
		this.lastname = lastname;
		this.roles = roles;
	}

	/**
	 * get the prename of the user.
	 *
	 * @return prename of user
	 */
	public String getPreName() {
		return prename;
	}

	/**
	 * get the lastname of the user.
	 *
	 * @return lastname of the user
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * get the permissions of the user.
	 *
	 * @return permissions of user
	 */
	public Set<String> getPermissions() {
		return roles;
	}

}
