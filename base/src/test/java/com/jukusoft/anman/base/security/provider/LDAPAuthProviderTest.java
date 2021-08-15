package com.jukusoft.anman.base.security.provider;

import com.jukusoft.anman.base.config.LDAPConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.test.EmbeddedLdapServer;
import org.springframework.ldap.test.LdapTestUtils;
import org.springframework.ldap.test.unboundid.LdifPopulator;
import org.springframework.ldap.test.unboundid.TestContextSourceFactoryBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/*
 * test class for ldap authentication provider.
 *
 * @author Justin Kuenzel
 */
@SpringBootTest
@ActiveProfiles({"test"})
@PropertySource(value = {"classpath:base.properties", "classpath:test-ldap.properties"}, ignoreResourceNotFound = false)
@PropertySources({
		@PropertySource(value = {"file:../local-properties.properties"}, ignoreResourceNotFound = true)
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
	 * the embedded ldap server port.
	 */
	private static int port;

	/**
	 * the instance of the embedded ldap server.
	 */
	private static EmbeddedLdapServer embeddedLdapServer;

	@BeforeAll
	public static void beforeAll() throws Exception {
		port = 389;//9000 + new Random().nextInt(1000);
		//embeddedLdapServer = EmbeddedLdapServer.newEmbeddedServer("example", "dc=localdomain,dc=local", port);
		//LdapTestUtils.startEmbeddedServer(port, "dc=localdomain,dc=local", "example");
	}

	@AfterAll
	public static void afterAll() throws Exception {
		if (embeddedLdapServer != null) {
			embeddedLdapServer.shutdown();
			embeddedLdapServer = null;
		}

		LdapTestUtils.shutdownEmbeddedServer();
	}

	/**
	 * this test checks, if the user login works, this means that the login only works, if the credentials are correct.
	 */
	@Test
	void testLDAPAuth() {
		//LdapTestUtils.startEmbeddedServer(port, "dc=localdomain,dc=local", "example");

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

	//@Bean
	public TestContextSourceFactoryBean testLDAPContextSource() throws Exception {
		TestContextSourceFactoryBean contextSource
				= new TestContextSourceFactoryBean();
		//contextSource.setContextSource(ldapConfig.ldapContextSource());

		contextSource.setDefaultPartitionName("example");
		contextSource.setDefaultPartitionSuffix("dc=localdomain,dc=local");
		contextSource.setPrincipal("uid=test,cn=users,cn=accounts");
		contextSource.setPassword("test1234");
		contextSource.setLdifFile(
				resourceLoader.getResource("example-ldap.ldif"));
		contextSource.setPort(port);
		contextSource.afterPropertiesSet();

		return contextSource;
	}

	//@Bean
	public LdifPopulator ldifPopulator(@Autowired EmbeddedLdapServer embeddedLdapServer) throws Exception {
		LdifPopulator ldifPopulator = new LdifPopulator();
		ldifPopulator.setResource(resourceLoader.getResource("example-ldap.ldif"));
		ldifPopulator.setBase("dc=localdomain,dc=local");
		ldifPopulator.setClean(true);
		ldifPopulator.setDefaultBase("dc=localdomain,dc=local");
		ldifPopulator.afterPropertiesSet();

		return ldifPopulator;
	}

}
