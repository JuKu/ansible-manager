package com.jukusoft.anman.base;

import com.jukusoft.anman.base.dao.RefreshDAOImpl;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.ldap.AutoConfigureDataLdap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/*
 * a test application for junit tests.
 *
 * @author Justin Kuenzel
 */
@SpringBootApplication(scanBasePackages = {"com.jukusoft.anman", "com.jukusoft.authentification.jwt"}, exclude = {
		LdapAutoConfiguration.class
})
@PropertySource({"classpath:base.properties"})
@EntityScan({"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
@EnableJpaRepositories(value = {"com.jukusoft.anman", "com.jukusoft.authentification.jwt"}, repositoryBaseClass = RefreshDAOImpl.class)
public class TestApplication {

	@Bean
	@ConditionalOnMissingBean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
