package com.jukusoft.anman.base.security.provider;

import com.jukusoft.anman.base.dao.CustomerDAO;
import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.general.CustomerEntity;
import com.jukusoft.anman.base.entity.user.RoleEntity;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.security.AccountService;
import com.jukusoft.anman.base.security.AuthProvider;
import com.jukusoft.anman.base.security.ExtendedAccountDTO;
import com.jukusoft.anman.base.utils.PasswordService;
import com.jukusoft.authentification.jwt.account.AccountDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

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
@DirtiesContext(classMode = AFTER_CLASS)
class LocalDatabaseAuthProviderTest {

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private UserDAO userDAO;

	/**
	 * the local database authentication provider to test.
	 */
	private static LocalDatabaseAuthProvider authProvider;

	/**
	 * the password service.
	 */
	private static PasswordService passwordService;

	@BeforeEach
	public void before() {
		//create authentication provider
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		passwordService = new PasswordService(passwordEncoder);
		String salt = passwordService.generateSalt();
		authProvider = new LocalDatabaseAuthProvider(userDAO, passwordService.getPasswordEncoder());

		//create customer
		CustomerEntity customer = new CustomerEntity("super-admin");
		customer = customerDAO.save(customer);

		//create some sample user entries
		UserEntity user = new UserEntity(customer, "test", "prename", "lastname");
		user.setPassword(passwordService.encodePassword("test1234", salt));
		user.setSalt(salt);

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
	void testProvider() {
		List<AuthProvider> authProviderList = new ArrayList<>();
		authProviderList.add(new LocalDatabaseAuthProvider(userDAO, new BCryptPasswordEncoder()));
		AccountService service = new AccountService(customerDAO, userDAO, authProviderList, "local-database");
		service.init();

		Optional<AccountDTO> accountDTO = service.loginUser("test", "test1234");
		assertTrue(accountDTO.isPresent());

		//second test
		authProviderList = new ArrayList<>();
		authProviderList.add(new DummyAuthProvider());
		service = new AccountService(customerDAO, userDAO, authProviderList, "dummy-auth-provider");
		service.init();

		accountDTO = service.loginUser("test5678", "test1234");
		assertFalse(accountDTO.isPresent());

		accountDTO = service.loginUser("test1234", "test1234");
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

	/*@SpringBootApplication(scanBasePackages = {"com.jukusoft.anman", "com.jukusoft.authentification.jwt"}, exclude = {
			LdapAutoConfiguration.class
	})
	@PropertySource({"classpath:base.properties"})
	@EntityScan({"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
	@EnableJpaRepositories({"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
	static class TestApplication {
		//
	}*/

}
