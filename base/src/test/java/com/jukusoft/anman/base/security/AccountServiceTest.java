package com.jukusoft.anman.base.security;

import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.authentification.jwt.account.AccountDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AccountServiceTest {

    /**
     * the password encoder.
     */
    private static PasswordEncoder passwordEncoder;

    @BeforeAll
    public static void beforeAll() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @AfterAll
    public static void afterAll() {
        passwordEncoder = null;
    }

    /**
     * verify, that the constructor works correct.
     */
    @Test
    public void testConstructor() {
        new AccountService(Mockito.mock(UserDAO.class), passwordEncoder);
    }

    /**
     * check, that a valid login works.
     */
    @Test
    public void testLogin() {
        UserDAO userDAO = Mockito.mock(UserDAO.class);
        UserEntity user = new UserEntity("test", "test", "test");
        String encodedPassword = passwordEncoder.encode("test1234");
        user.setPassword(encodedPassword);
        user.forceID(2);
        when(userDAO.findOneByUsername(anyString())).thenReturn(Optional.of(user));

        assertTrue(passwordEncoder.matches( "test1234", encodedPassword));

        AccountService service = new AccountService(userDAO, passwordEncoder);
        Optional<AccountDTO> accountDTO = service.loginUser("test", "test1234");
        assertTrue(accountDTO.isPresent());
        assertEquals(2, accountDTO.get().getUserID());
    }

    @Test
    public void testWrongLogin() {
        UserDAO userDAO = Mockito.mock(UserDAO.class);
        UserEntity user = new UserEntity("test", "test", "test");
        String encodedPassword = passwordEncoder.encode("test5678");
        user.setPassword(encodedPassword);
        when(userDAO.findOneByUsername(anyString())).thenReturn(Optional.of(user));

        assertFalse(passwordEncoder.matches( "test1234", encodedPassword));
        AccountService service = new AccountService(userDAO, passwordEncoder);
        Optional<AccountDTO> accountDTO = service.loginUser("test", "test1234");
        assertTrue(accountDTO.isEmpty());
    }

}
