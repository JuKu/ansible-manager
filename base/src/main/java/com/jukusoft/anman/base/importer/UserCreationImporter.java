package com.jukusoft.anman.base.importer;

import com.jukusoft.anman.base.dao.CustomerDAO;
import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.general.CustomerEntity;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.utils.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	 * the customer dao.
	 */
	private CustomerDAO customerDAO;

	/**
	 * the user dao.
	 */
	private UserDAO userDAO;

	/**
	 * the password service for password encoding and salt generation.
	 */
	private PasswordService passwordService;

	/**
	 * the admin username.
	 * Default: "admin".
	 */
	@Value("${admin.user}")
	private String adminUser = "admin";

	/**
	 * the admin password.
	 * Default: "admin".
	 */
	@Value("${admin.password}")
	private String adminPassword = "admin";

	/**
	 * constructor.
	 *
	 * @param customerDAO customer dao
	 * @param userDAO user dao
	 * @param passwordService password service
	 */
	public UserCreationImporter(@Autowired CustomerDAO customerDAO, @Autowired UserDAO userDAO, @Autowired PasswordService passwordService) {
		this.customerDAO = customerDAO;
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
		//check, if min. one customer exists, else create the super admin customer
		if (customerDAO.count() == 0) {
			logger.info("create first super admin customer");

			CustomerEntity customer = new CustomerEntity("super-admin");
			customer.setDeletable(false);
			customer.setEditable(false);
			customerDAO.save(customer);
		}

		//check, if min. one user exists, else create admin user
		if (userDAO.count() == 0) {
			logger.info("create admin user 'admin'");

			String salt = passwordService.generateSalt();

			//create demo user and add it to first customer
			UserEntity user = new UserEntity(customerDAO.findOneByName("super-admin").get(), "admin", "Admin", "Admin");
			user.setSalt(salt);
			user.setPassword(passwordService.encodePassword("admin", salt));
			user = userDAO.save(user);

			if (user.getId() == 0) {
				throw new IllegalStateException("user id is was not automatically inserted by Spring JPA");
			}
		}
	}

}
