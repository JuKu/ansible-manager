package com.jukusoft.anman.base.security.provider;

import com.jukusoft.anman.base.config.LDAPConfig;
import com.jukusoft.anman.base.security.AuthProvider;
import com.jukusoft.anman.base.security.ExtendedAccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

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
			//find user - if the credentials are wrong, this method throws an exception
			DirContext dirContext = ldapContextSource.getContext(generateUserCN(username), password);

			//create ldap context source to filter groups
			String userCN = generateUserCN(username);

			//remove base dn (+ comma) from the string
			userCN = userCN.substring(0, userCN.length() - (ldapConfig.getLdapBase().length() + 1));
			LdapContextSource source = ldapConfig.createContextSourceWithAuthentication(userCN, password);
			LdapTemplate template = new LdapTemplate(source);

			//append a "ldap-" prefix to all groups from ldap
			Set<String> roles = getUserGroupsFromLDAP(template, username).stream()
					.map(group -> "ldap-" + group)
					.collect(Collectors.toSet());

			//login successfully
			LOGGER.info("ldap login successful for user: {}", generateUserCN(username));
			ExtendedAccountDTO accountDTO = new ExtendedAccountDTO(-1, username, username, username, roles);
			return Optional.of(accountDTO);
		} catch (CommunicationException e) {
			LOGGER.warn("Cannot reach LDAP server: " + ldapContextSource.getUrls()[0], e);
			throw new IllegalStateException("ldap server is not reachable: " + ldapContextSource.getUrls()[0]);
		} catch (AuthenticationException e) {
			//credentials are wrong
			LOGGER.info("ldap credentials are wrong for user: {}", generateUserCN(username), e);
			return Optional.empty();
		}
	}

	/**
	 * generate a full user cn from username.
	 *
	 * @param username username
	 *
	 * @return full user cn, e.q. uid=testuser,cn=users,cn=accounts,dc=localdomain,dc=local
	 */
	protected String generateUserCN(String username) {
		//expected form: uid=<User>,cn=users,cn=accounts,dc=localdomain,dc=local
		return userOUType + "=" + username + "," + usersOU + "," + ldapConfig.getLdapBase();
	}

	/**
	 * get all groups of user from LDAP.
	 *
	 * @param template ldap template
	 * @param username the username
	 *
	 * @return list with all groups of user from ldap
	 */
	protected List<String> getUserGroupsFromLDAP(LdapTemplate template, String username) {
		Objects.requireNonNull(template);
		Objects.requireNonNull(username);

		//Get the attribute of user's "memberOf"
		List<String> membersOf = template.search(
				query().base(ldapConfig.getUsersOU() + "," + ldapConfig.getLdapBase()).where(ldapConfig.getUserIDType()).is(username),
				(AttributesMapper<ArrayList<?>>) attrs -> Collections.list(attrs.get("memberOf").getAll())
		).get(0)
				.stream()
				.map(entry -> entry.toString())
				.toList();

		String groupPrefix = "cn=";
		String groupSuffix = "," + ldapConfig.getGroupSuffix();

		//check, if groups ends with expected suffix
		List<String> groups = membersOf.stream()
				.filter(entry -> entry.endsWith(groupSuffix))
				.map(entry -> entry.substring(groupPrefix.length()))
				.map(entry -> entry.substring(0, entry.length() - groupSuffix.length()))//remove suffix
				.toList();

		return groups;
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
