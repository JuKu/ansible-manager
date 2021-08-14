package com.jukusoft.anman.base.security.provider;

import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.RoleEntity;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.security.ExtendedAccountDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/*
 *
 *
 * @author Justin Kuenzel
 */
@DataJpaTest(properties = {
		"spring.test.database.replace=NONE",
		"spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"
})
@ActiveProfiles({"test"})
class LocalDatabaseAuthProviderTest {

	@Autowired
	private UserDAO userDAO;

	/**
	 * the local database authentication provider to test.
	 */
	private static LocalDatabaseAuthProvider authProvider;

	@BeforeEach
	public void before() {
		//create authentication provider
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		authProvider = new LocalDatabaseAuthProvider(userDAO, new BCryptPasswordEncoder());

		//create some sample user entries
		UserEntity user = new UserEntity("test", "prename", "lastname");
		user.setPassword(passwordEncoder.encode("test1234"));

		RoleEntity role = new RoleEntity("test-role1");
		RoleEntity role1 = new RoleEntity("test-role2");
		RoleEntity role2 = new RoleEntity("test-role3");

		user.addRole(role);
		user.addRole(role1);
		user.addRole(role2);

		userDAO.save(user);
	}

	@AfterEach
	public void after() {
		authProvider = null;
	}

	@Test
	void injectedComponentsAreNotNull() {
		assertNotNull(userDAO);

		//verify, that the test user exists
		assertNotNull(userDAO.findOneByUsername("test"));
	}

	/**
	 * check, that the authentication provider name did not changed.
	 */
	@Test
	void testName() {
		assertEquals("local-database", authProvider.getName());
	}

	@Test
	void testLogin() {
		//try to login with wrong credentials
		Optional<ExtendedAccountDTO> accountDTO = authProvider.login("test", "test5678");
		assertTrue(accountDTO.isEmpty());

		//try to login with correct credentials
		accountDTO = authProvider.login("test", "test1234");
		assertTrue(accountDTO.isPresent());
	}

	@Test
	void testListRoles() {
		Set<String> roles = authProvider.listRoles("test");
		assertFalse(roles.isEmpty());
		assertEquals(3, roles.size());

		assertFalse(roles.stream().anyMatch(role -> role.equals("test-role")));
		assertTrue(roles.stream().anyMatch(role -> role.equals("test-role1")));
	}

	@Test
	void testHasRole() {
		assertFalse(authProvider.hasRole("test", "test-role"));
		assertTrue(authProvider.hasRole("test", "test-role1"));
		assertThrows(NoSuchElementException.class, () -> authProvider.hasRole("test1234", "test-role1"));
		assertTrue(authProvider.hasRole("test", "test-role2"));
	}

	@SpringBootApplication(scanBasePackages = {"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
	@PropertySource({"classpath:base.properties"})
	@EntityScan({"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
	@EnableJpaRepositories({"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
	static class TestApplication {
		//
	}

}
