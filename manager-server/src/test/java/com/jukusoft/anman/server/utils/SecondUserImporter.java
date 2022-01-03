package com.jukusoft.anman.server.utils;

import com.jukusoft.anman.base.dao.CustomerDAO;
import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.transaction.Transactional;

/**
 * Imports a second user, used for some junit tests.
 * See also TeamControllerTest.
 *
 * @author Justin Kuenzel
 */
@Configuration
@Profile({"junit-test-create-second-user"})
public class SecondUserImporter implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecondUserImporter.class);

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private UserDAO userDAO;

	@Override
	@Transactional
	public void run(ApplicationArguments args) throws Exception {
		String username = "new-user";
		LOGGER.info("import second user '{}'", username);

		UserEntity user = createNewUser(username);
	}

	@Transactional(value = Transactional.TxType.REQUIRES_NEW)
	protected UserEntity createNewUser(String username) {
		UserEntity newUser = new UserEntity(customerDAO.findOneByName("super-admin").orElseThrow(), username, "New-User", "New-User");
		newUser = userDAO.save(newUser);

		return newUser;
	}

}
