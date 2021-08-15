package com.jukusoft.anman.base.security.provider;

import com.jukusoft.anman.base.config.LDAPConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ldap.test.unboundid.TestContextSourceFactoryBean;

/*
 * test class for ldap authentication provider.
 *
 * @author Justin Kuenzel
 */
@SpringBootTest
@PropertySource("classpath:test-ldap.properties")
public class LDAPAuthProviderTest {

	@Autowired
	private LDAPConfig ldapConfig;

	@Test
	void testLDAPAuth() {
		//
	}

	@Bean
	public TestContextSourceFactoryBean testContextSource() {
		TestContextSourceFactoryBean contextSource
				= new TestContextSourceFactoryBean();
		contextSource.setContextSource(ldapConfig.contextSource());

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
