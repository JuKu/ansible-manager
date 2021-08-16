package com.jukusoft.anman.base.importer;

import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.utils.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/*
 * an importer which creates the admin user, if no user exists.
 *
 * @author Justin Kuenzel
 */
@Configuration
@Profile("default")
public class UserCreationImporter implements InitializingBean {

	/**
	 * the logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(UserCreationImporter.class);

	/**
	 * the user dao
	 */
	private UserDAO userDAO;

	/**
	 * the password service for password encoding and salt generation.
	 */
	private PasswordService passwordService;

	/**
	 * constructor.
	 *
	 * @param userDAO user dao
	 * @param passwordService password service
	 */
	public UserCreationImporter(@Autowired UserDAO userDAO, @Autowired PasswordService passwordService) {
		this.userDAO = userDAO;
		this.passwordService = passwordService;
	}

	/**
	 * this method is called from spring after startup.
	 *
	 * @throws Exception if the user creation failed
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		//check, if min. one user exists, else create admin user
		if (userDAO.count() == 0) {
			logger.info("create admin user 'admin'");

			String salt = passwordService.generateSalt();

			//create demo user and add it to first customer
			UserEntity user = new UserEntity("admin", "Admin", "Admin");
			user.setSalt(salt);
			user.setPassword(passwordService.encodePassword("admin", salt));
			user = userDAO.save(user);

			if (user.getId() == 0) {
				throw new IllegalStateException("user id is was not automatically inserted by Spring JPA");
			}
		}
	}

}
