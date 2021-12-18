package com.jukusoft.anman.base.utils;

import com.jukusoft.anman.base.dao.CustomerDAO;
import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.general.CustomerEntity;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.importer.UserCreationImporter;
import com.jukusoft.anman.base.teams.TeamDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

/**
 * An abstract test utility class for H2 database tests.
 *
 * @author Justin Kuenzel
 */
@DataJpaTest(properties = {
		"spring.test.database.replace=NONE",
		"spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"
})
@ActiveProfiles({"test"})
@DirtiesContext(classMode = AFTER_CLASS)
public class DBTest {

	@Autowired
	protected CustomerDAO customerDAO;

	@Autowired
	protected TeamDAO teamDAO;

	@Autowired
	protected UserDAO userDAO;

	@Autowired
	protected TestEntityManager testEntityManager;

	protected void flushDB() {
		//see also: https://josefczech.cz/2020/02/02/datajpatest-testentitymanager-flush-clear/

		// forces synchronization to DB
		testEntityManager.flush();

		// clears persistence context
		// all entities are now detached and can be fetched again
		testEntityManager.clear();
	}

	protected void importDefaultCustomer() throws Exception {
		//first, import the default customer
		UserCreationImporter userCreationImporter = new UserCreationImporter(customerDAO, userDAO, new PasswordService(new BCryptPasswordEncoder()));
		userCreationImporter.afterPropertiesSet();

		flushDB();
	}

	protected CustomerEntity getDefaultCustomer() {
		return customerDAO.findOneByName("super-admin").orElseThrow();
	}

	protected UserEntity getDefaultUser() {
		return userDAO.findOneByUsername("admin").orElseThrow();
	}

	protected void cleanUp() {
		flushDB();

		customerDAO.deleteAll();
		teamDAO.deleteAll();
		userDAO.deleteAll();
	}

}
