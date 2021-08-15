package com.jukusoft.anman.base.security.provider;

import com.jukusoft.anman.base.config.LDAPConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ldap.test.unboundid.TestContextSourceFactoryBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

/*
 * test class for ldap authentication provider.
 *
 * @author Justin Kuenzel
 */
@SpringBootTest
@ActiveProfiles({"test"})
@PropertySource({"classpath:base.properties", "classpath:test-ldap.properties"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class LDAPAuthProviderTest {

	@Autowired
	private LDAPConfig ldapConfig;

	@Test
	void testLDAPAuth() {
		//
	}

	@Bean
	public TestContextSourceFactoryBean testLDAPContextSource() {
		TestContextSourceFactoryBean contextSource
				= new TestContextSourceFactoryBean();
		contextSource.setContextSource(ldapConfig.ldapContextSource());

		/*contextSource.setDefaultPartitionName(
				env.getRequiredProperty("ldap.partition"));
		contextSource.setDefaultPartitionSuffix(
				env.getRequiredProperty("ldap.partitionSuffix"));
		contextSource.setPrincipal("admin");
		contextSource.setPassword("secret");*/
		/*contextSource.setLdifFile(
				resourceLoader.getResource(
						env.getRequiredProperty("ldap.ldiffile")));*/
		contextSource.setPort(389);
		return contextSource;
	}

}
