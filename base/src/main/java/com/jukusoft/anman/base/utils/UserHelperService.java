package com.jukusoft.anman.base.utils;

import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.authentification.jwt.account.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * a utility service to get the current user of the current context.
 *
 * @author Justin Kuenzel
 */
@Service
public class UserHelperService {

    /**
     * the user dao.
     */
    private UserDAO userDAO;

    /**
     * constructor.
     *
     * @param userDAO user dao
     */
    public UserHelperService(@Autowired UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * get current user.
     *
     * @return the current logged in user
     */
    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Objects.requireNonNull(authentication);

        UserAccount userAccount = (UserAccount) authentication;

        //find current userID
        long userID = userAccount.getUserID();

        return getUserById(userID).orElseThrow();
    }

	public long getCurrentUserID() {
		return getCurrentUser().getUserID();
	}

	public Optional<UserEntity> getUserById(long userID) {
		//TODO: add caching

		return userDAO.findById(userID);
	}

}
