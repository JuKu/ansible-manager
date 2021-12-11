package com.jukusoft.anman.base.utils;

import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.authentification.jwt.account.UserAccount;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/*
 * test class for UserHelperService, which is responsible for getting the current, logged in UserEntity object.
 *
 * @author Justin Kuenzel
 */
class UserHelperServiceTest {

	/**
	 * check to get the current logged-in user.
	 */
	@Test
	void testGetCurrentUser() {
		UserDAO userDAO = Mockito.mock(UserDAO.class);
		UserHelperService service = new UserHelperService(userDAO);

		UserAccount userAccount = Mockito.mock(UserAccount.class);
		when(userAccount.getUserID()).thenReturn(1l);

		UserEntity userEntity = Mockito.mock(UserEntity.class);
		when(userDAO.findById(1l)).thenReturn(Optional.empty());
		when(userDAO.findById(2l)).thenReturn(Optional.of(userEntity));

		SecurityContextHolder.getContext().setAuthentication(userAccount);
		assertThrows(NoSuchElementException.class, () -> service.getCurrentUser());

		when(userAccount.getUserID()).thenReturn(2l);
		SecurityContextHolder.getContext().setAuthentication(userAccount);
		assertNotNull(service.getCurrentUser());
		assertEquals(userEntity, service.getCurrentUser());
	}

	@Test
	void testGetUserById() {
		UserDAO userDAO = Mockito.mock(UserDAO.class);
		when(userDAO.findById(anyLong())).thenReturn(Optional.of(Mockito.mock(UserEntity.class)));
		UserHelperService service = new UserHelperService(userDAO);
		assertNotNull(service.getUserById(1l));
	}

}
