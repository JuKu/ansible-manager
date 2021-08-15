package com.jukusoft.anman.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/*
 * ldap configuration.
 *
 * @author Justin Kuenzel
 */
@Configuration
public class LDAPConfig {

	/**
	 * the ldap url, e.q. "ldap://localhost:389/dc=localdomain,dc=local".
	 *
	 * See also: https://stackoverflow.com/questions/52153346/how-to-configure-spring-to-use-external-ldap-server
	 */
	@Value("${ldap.url}")
	private String ldapUrl;

	/**
	 * the ldap base (suffix).
	 */
	@Value("${ldap.base}")
	private String ldapBase;

	/**
	 * the ldap principal (admin user).
	 */
	@Value("${ldap.principal}")
	private String ldapPricipal;

	/**
	 * the ldap password.
	 */
	@Value("${ldap.password}")
	private String ldapPassword;

	/**
	 * set the ldap context source, if ldap is enabled.
	 *
	 * @return ldap context source
	 */
	@Bean
	@Conditional(LDAPCondition.class)
	public LdapContextSource ldapContextSource() {
		LdapContextSource contextSource = new LdapContextSource();

		contextSource.setUrl(ldapUrl);
		contextSource.setBase(ldapBase);
		contextSource.setUserDn(ldapPricipal);
		contextSource.setPassword(ldapPassword);

		return contextSource;
	}

	/**
	 * create a ldap template bean.
	 *
	 * @return ldap template
	 */
	@Bean
	@Conditional(LDAPCondition.class)
	public LdapTemplate ldapTemplate() {
		return new LdapTemplate(ldapContextSource());
	}

}
