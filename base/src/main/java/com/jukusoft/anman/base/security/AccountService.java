package com.jukusoft.anman.base.security;

import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.authentification.jwt.account.AccountDTO;
import com.jukusoft.authentification.jwt.account.IAccountService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * the account service which is responsible for authentication.
 *
 * @author Justin Kuenzel
 */
@Service
@Qualifier("iAccountService")
public class AccountService implements IAccountService {

    /**
     * user DAO.
     */
    private UserDAO userDAO;

    /**
     * the password encoder.
     */
    private PasswordEncoder passwordEncoder;

    /**
     * constructor.
     *
     * @param userDAO
     * @param passwordEncoder
     */
    public AccountService(@Autowired UserDAO userDAO, @Autowired(required = false) PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
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
        return userDAO.findOneByUsername(username)
                .filter(account -> passwordEncoder.matches(password, account.getPassword()))
                .map(user -> new AccountDTO(user.getId(), user.getUsername()));
    }

}
