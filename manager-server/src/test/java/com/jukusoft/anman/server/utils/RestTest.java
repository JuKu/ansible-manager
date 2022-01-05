package com.jukusoft.anman.server.utils;

import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Justin Kuenzel
 */
@ActiveProfiles({"test"})
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = RestTest.TestApplication.class)
public abstract class RestTest {

	@Autowired
	protected MockMvc mockMvc;

	@TestConfiguration
	static class TestConfig {

		/*@Bean
		public MeterRegistry meterRegistry() {
			return new SimpleMeterRegistry();
		}*/

	}

	@SpringBootApplication(scanBasePackages = {"com.jukusoft.anman.server.controller"})
	@PropertySource({"classpath:base.properties"})
	public static class TestApplication {

		@Bean
		@ConditionalOnMissingBean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}

		@Bean
		public WebSecurityConfigurerAdapter testSecurityConfig() {
			return new TestSecurityConfig();
		}

	}

	@TestConfiguration
	@Order(1)
	public static class TestSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity httpSecurity) throws Exception {
			// Disable CSRF
			httpSecurity.csrf().disable()
					// Permit all requests without authentication
					.authorizeRequests().anyRequest().permitAll();
		}
	}

}
