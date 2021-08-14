package com.jukusoft.anman.base.security;/*
 *
 *
 * @author Justin Kuenzel
 */

import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.PermissionEntity;
import com.jukusoft.anman.base.entity.user.RoleEntity;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.authentification.jwt.account.UserAccount;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * test class for the session service implementation.
 *
 * @author Justin Kuenzel
 */
public class SessionServiceImplTest {

    /**
     * the session service to test.
     */
    private static SessionServiceImpl sessionService;

    @BeforeAll
    public static void beforeAll() {
        UserDAO userDAO = Mockito.mock(UserDAO.class);
        UserEntity userEntity = new UserEntity("test", "test", "test");
        userEntity.forceID(1);

        RoleEntity roleEntity = new RoleEntity("test-role");
        roleEntity.addPermission(new PermissionEntity("test", "test", PermissionEntity.PermissionType.GLOBAL));
        userEntity.addRole(roleEntity);

        when(userDAO.findOneByUsername("test")).thenReturn(Optional.of(userEntity));
        when(userDAO.findById(anyLong())).thenReturn(Optional.of(userEntity));
        sessionService = new SessionServiceImpl(userDAO);
    }

    @AfterAll
    public static void afterAll() {
        sessionService = null;
    }

    /**
     * tests, that a null user throws an NPE.
     */
    @Test
    public void testFindNullUser() {
        assertThrows(NullPointerException.class, () -> sessionService.findUser(null));
        assertThrows(IllegalStateException.class, () -> sessionService.findUser(""));
    }

    /**
     * test, that a unknown user throws an exception.
     */
    @Test
    public void testFindUnknownUser() {
        assertThrows(IllegalStateException.class, () -> sessionService.findUser("unknown-user"));
    }

    /**
     * test, that find a known user returns an user entity object
     */
    @Test
    public void testFindUser() {
        UserAccount userAccount = sessionService.findUser("test");
        assertNotNull(userAccount);
        assertEquals(UserAccount.class, userAccount.getClass());
    }

}
