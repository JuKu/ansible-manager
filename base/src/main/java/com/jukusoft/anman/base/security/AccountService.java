package com.jukusoft.anman.base.security;

import com.jukusoft.anman.base.dao.CustomerDAO;
import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.authentification.jwt.account.AccountDTO;
import com.jukusoft.authentification.jwt.account.IAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * the account service which is responsible for authentication.
 *
 * @author Justin Kuenzel
 */
@Service
@Qualifier("iAccountService")
public class AccountService implements IAccountService {

	/**
	 * the logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

	/**
	 * the customer dao.
	 */
	private CustomerDAO customerDAO;

	/**
	 * the user dao.
	 */
	private UserDAO userDAO;

	/**
	 * a list with all pluggable authentication providers, like local database, ldap and so on.
	 */
	private List<AuthProvider> authProviderList;

	/**
	 * a comma-seperated list of activated authentication providers.
	 */
	private String authProviderConfig;

	/**
	 * constructor.
	 *
	 * @param authProviders list with authentication providers
	 */
	public AccountService(@Autowired CustomerDAO customerDAO, @Autowired UserDAO userDAO, @Autowired List<AuthProvider> authProviders, @Value("${auth.providers}") String authProviderConfig) {
		Objects.requireNonNull(authProviderConfig);

		if (authProviderConfig.isEmpty()) {
			throw new IllegalArgumentException("auth provider config cannot be null");
		}

		this.customerDAO = customerDAO;
		this.userDAO = userDAO;
		this.authProviderList = authProviders;
		this.authProviderConfig = authProviderConfig;
	}

	/**
	 * initialize the bean.
	 */
	@PostConstruct
	public void init() {
		//check, that minimum one authentication provider is available
		if (authProviderList.isEmpty()) {
			throw new IllegalStateException("no authentication provider in classpath");
		}

		int pluginsFound = authProviderList.size();

		//remove authentication providers, which are not enabled
		Set<String> enabledAuthProviders = Set.of(authProviderConfig.split(","));

		//we need a new list to avoid ConcurrentModificationException while removing entries from the list
		List<AuthProvider> authProviders = new ArrayList<>(authProviderList);

		//remove disabled auth providers from list
		for (AuthProvider authProvider : authProviders) {
			Objects.requireNonNull("name of authentication provider is null: " + authProvider.getClass().getCanonicalName(), authProvider.getName());

			if (!enabledAuthProviders.contains(authProvider.getName())) {
				authProviderList.remove(authProvider);
			}
		}

		LOGGER.info("{} auth providers found, {} auth providers are enabled: {}", pluginsFound, authProviderList.size(), authProviderConfig);

		if (authProviderList.isEmpty()) {
			throw new IllegalStateException("%s authentication providers available, but no provider is enabled".formatted(pluginsFound));
		}

		//first, sort list by priority
		Collections.sort(authProviderList);
	}

	/**
	 * try to login the user.
	 *
	 * @param username username
	 * @param password password
	 *
	 * @return optional with account, if credentials are correct, else empty optional
	 */
	@Override
	public Optional<AccountDTO> loginUser(String username, String password) {
		LOGGER.info("try to login user: {}", username);

		for (AuthProvider authProvider : authProviderList) {
			//try to login
			Optional<ExtendedAccountDTO> accountDTO = authProvider.login(username, password);

			if (accountDTO.isPresent()) {
				//check, if user exists in database
				if (userDAO.findOneByUsername(username).isEmpty()) {
					LOGGER.info("login user '{}' successfully, but user does not exists in database - create user in database now", username);
					UserEntity user = new UserEntity(customerDAO.findOneByName("super-admin").get(), username, accountDTO.get().getPreName(), accountDTO.get().getLastname());
					user.setPassword("not_local");
					userDAO.save(user);
				}

				return Optional.of(accountDTO.get());
			}
		}

		return Optional.empty();
	}

}
