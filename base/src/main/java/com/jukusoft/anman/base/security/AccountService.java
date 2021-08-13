package com.jukusoft.anman.base.security;

import com.jukusoft.authentification.jwt.account.AccountDTO;
import com.jukusoft.authentification.jwt.account.IAccountService;
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

    //@Autowired
    //private UserDAO userDAO;

    /**
     * the password encoder.
     */
    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

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
        //TODO: add code here

        /*return userDAO.findOneByUsername(username)
                .filter(account -> passwordEncoder.matches(password, account.getPassword()))
                .map(user -> new AccountDTO(user.getId(), user.getUsername()));*/
        return Optional.empty();
    }

}
