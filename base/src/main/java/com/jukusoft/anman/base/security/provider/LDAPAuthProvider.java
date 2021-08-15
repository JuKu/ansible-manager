package com.jukusoft.anman.base.security.provider;

import com.jukusoft.anman.base.config.LDAPConfig;
import com.jukusoft.anman.base.security.AuthProvider;
import com.jukusoft.anman.base.security.ExtendedAccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Service;

import javax.naming.directory.DirContext;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

/*
 * a ldap authentication provider to authenticate against a LDAP server.
 *
 * @author Justin Kuenzel
 */
@Service
public class LDAPAuthProvider implements AuthProvider {

	/**
	 * the logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LDAPAuthProvider.class);

	/**
	 * the ldap context source, required for authentication.
	 */
	private LdapContextSource ldapContextSource;

	/**
	 * the ldap template, created in LDAPConfig.
	 *
	 * @see LDAPConfig#ldapTemplate()
	 */
	private LdapTemplate ldapTemplate;

	/**
	 * the ldap configuration.
	 */
	private LDAPConfig ldapConfig;

	/**
	 * users organizational unit.
	 */
	private String usersOU;

	/**
	 * the user ou type, e.q. "cn", "uid" or "uuid"
	 */
	private String userOUType;

	/**
	 * constructor.
	 *
	 * @param ldapTemplate ldap template
	 */
	public LDAPAuthProvider(@Autowired LdapContextSource ldapContextSource, @Autowired LdapTemplate ldapTemplate, @Autowired LDAPConfig ldapConfig) {
		Objects.requireNonNull(ldapContextSource);
		Objects.requireNonNull(ldapTemplate);
		this.ldapContextSource = ldapContextSource;
		this.ldapTemplate = ldapTemplate;
		this.ldapConfig = ldapConfig;
		this.usersOU = ldapConfig.getUsersOU();
		this.userOUType = ldapConfig.getUserOUType();
	}

	@Override
	public String getName() {
		return "ldap";
	}

	@Override
	public Optional<ExtendedAccountDTO> login(String username, String password) {
		LOGGER.info("try to authenticate user '{}' with ldap server: {}", username, ldapContextSource.getUrls()[0]);

		//set ldap timeout, default: 5 seconds
		ldapTemplate.setDefaultTimeLimit(5000);

		try {
			DirContext dirContext = ldapContextSource.getContext(generateUserCN(username), password);

			//login successfully
			LOGGER.info("ldap login successful for user: {}", generateUserCN(username));
			ExtendedAccountDTO accountDTO = new ExtendedAccountDTO(-1, username, username, username, new HashSet<>());
			return Optional.of(accountDTO);
		} catch (CommunicationException e) {
			LOGGER.warn("Cannot reach LDAP server: " + ldapContextSource.getUrls()[0], e);
			throw new IllegalStateException("ldap server is not reachable: " + ldapContextSource.getUrls()[0]);
		} catch (AuthenticationException e) {
			//credentials are wrong
			LOGGER.info("ldap credentials are wrong for user: {}", generateUserCN(username));
			return Optional.empty();
		}
	}

	protected String generateUserCN(String username) {
		//expected form: uid=<User>,cn=users,cn=accounts,dc=localdomain,dc=local
		return userOUType + "=" + username + "," + usersOU + "," + ldapConfig.getLdapBase();
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
