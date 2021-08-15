package com.jukusoft.anman.base.security.provider;

import com.jukusoft.anman.base.config.LDAPConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.test.unboundid.TestContextSourceFactoryBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/*
 * test class for ldap authentication provider.
 *
 * @author Justin Kuenzel
 */
@SpringBootTest
@ActiveProfiles({"test"})
@PropertySources({
		@PropertySource(value = {"file:../local-properties.properties"}, ignoreResourceNotFound = true),
		@PropertySource({"classpath:base.properties", "classpath:test-ldap.properties"})
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class LDAPAuthProviderTest {

	@Autowired
	private LDAPConfig ldapConfig;

	@Autowired
	private LdapContextSource ldapContextSource;

	@Autowired
	private LdapTemplate ldapTemplate;

	/**
	 * resource loader for example ldif file, see also: https://www.baeldung.com/spring-classpath-file-access
	 */
	@Autowired
	ResourceLoader resourceLoader;

	/**
	 * this test checks, if the user login works, this means that the login only works, if the credentials are correct.
	 */
	@Test
	void testLDAPAuth() {
		if (!new File("../local-properties.properties").exists()) {
			System.err.println("no local-properties.properties found, skip ldap tests");
			return;
		}

		//check, if ldap config was created
		assertNotNull(ldapConfig);
		assertNotNull(ldapConfig.ldapContextSource());
		assertNotNull(ldapConfig.ldapTemplate());

		LDAPAuthProvider authProvider = new LDAPAuthProvider(ldapContextSource, ldapTemplate, ldapConfig);

		//check login
		assertFalse(authProvider.login("test", "test5678").isPresent());
		assertTrue(authProvider.login("testuser", "test1234").isPresent());
	}

	@Test
	void testUserCNGeneration() {
		LDAPAuthProvider authProvider = new LDAPAuthProvider(ldapContextSource, ldapTemplate, ldapConfig);
		String userString = authProvider.generateUserCN("testuser");

		assertEquals("uid=testuser,cn=users,cn=accounts," + ldapConfig.getLdapBase(), userString);
		assertTrue(userString.contains("dc="));
	}

	@Bean
	public TestContextSourceFactoryBean testLDAPContextSource() {
		TestContextSourceFactoryBean contextSource
				= new TestContextSourceFactoryBean();
		contextSource.setContextSource(ldapConfig.ldapContextSource());

		/*contextSource.setDefaultPartitionName(
				env.getRequiredProperty("ldap.partition"));*/
		contextSource.setDefaultPartitionSuffix("dc=localdomain,dc=local");
		contextSource.setPrincipal("admin");
		contextSource.setPassword("secret");
		contextSource.setLdifFile(
				resourceLoader.getResource("example-ldap.ldif"));
		contextSource.setPort(389);
		return contextSource;
	}

}
