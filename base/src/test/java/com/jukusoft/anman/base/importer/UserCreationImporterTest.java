package com.jukusoft.anman.base.importer;

import com.jukusoft.anman.base.dao.CustomerDAO;
import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.security.provider.LocalDatabaseAuthProvider;
import com.jukusoft.anman.base.utils.PasswordService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

/*
 * test class for user creation importer.
 *
 * @author Justin Kuenzel
 */
@DataJpaTest(properties = {
		"spring.test.database.replace=NONE",
		"spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"
})
@ActiveProfiles({"test"})
@DirtiesContext(classMode = AFTER_CLASS)
class UserCreationImporterTest {

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private UserDAO userDAO;

	/**
	 * test, if the admin user is created on startup
	 */
	@Test
	void testUserCreation() throws Exception {
		PasswordService passwordService = new PasswordService(new BCryptPasswordEncoder());

		//check, that the users table is empty
		assertEquals(0, userDAO.count());

		UserCreationImporter importer = new UserCreationImporter(customerDAO, userDAO, passwordService);
		importer.afterPropertiesSet();

		assertEquals(1, userDAO.count());
		UserEntity user = userDAO.findOneByUsername("admin").orElseThrow();
		assertEquals("admin", user.getUsername());
		assertTrue(user.isPersistent());

		//try to login
		LocalDatabaseAuthProvider authProvider = new LocalDatabaseAuthProvider(userDAO, passwordService.getPasswordEncoder());
		assertFalse(authProvider.login("admin1", "admin").isPresent());
		assertFalse(authProvider.login("admin", "admin1").isPresent());
		assertTrue(authProvider.login("admin", "admin").isPresent());

		//execute importer again
		importer.afterPropertiesSet();

		//there should not be an additional user
		assertEquals(1, userDAO.count());

		//check, if an exception is thrown, if the user cannot be stored in database
		UserDAO userDAO1 = Mockito.mock(UserDAO.class);

		//return same UserEntity object without store them in database, so id will not be changed
		when(userDAO1.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		UserCreationImporter importer1 = new UserCreationImporter(customerDAO, userDAO1, passwordService);
		assertThrows(IllegalStateException.class, () -> importer1.afterPropertiesSet());
	}

}
