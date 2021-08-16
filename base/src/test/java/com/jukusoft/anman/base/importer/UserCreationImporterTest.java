package com.jukusoft.anman.base.importer;

import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.security.provider.LocalDatabaseAuthProvider;
import com.jukusoft.anman.base.utils.PasswordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
	private UserDAO userDAO;

	/**
	 * test, if the admin user is created on startup
	 */
	@Test
	void testUserCreation() throws Exception {
		PasswordService passwordService = new PasswordService(new BCryptPasswordEncoder());

		//check, that the users table is empty
		assertEquals(0, userDAO.count());

		UserCreationImporter importer = new UserCreationImporter(userDAO, passwordService);
		importer.afterPropertiesSet();

		assertEquals(1, userDAO.count());
		UserEntity user = userDAO.findOneByUsername("admin").orElseThrow();
		assertEquals("admin", user.getUsername());
		assertTrue(user.isPersistent());

		//try to login
		LocalDatabaseAuthProvider authProvider = new LocalDatabaseAuthProvider(userDAO, passwordService.getPasswordEncoder());
		assertTrue(authProvider.login("admin", "admin").isPresent());
	}

}
