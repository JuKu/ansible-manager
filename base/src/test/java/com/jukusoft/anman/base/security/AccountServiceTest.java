package com.jukusoft.anman.base.security;

import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.security.provider.DummyAuthProvider;
import com.jukusoft.anman.base.security.provider.LocalDatabaseAuthProvider;
import com.jukusoft.authentification.jwt.account.AccountDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AccountServiceTest {

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
    void testConstructor() {
        List<AuthProvider> authProviderList = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> new AccountService(Mockito.mock(UserDAO.class), authProviderList, ""));
        AccountService service = new AccountService(Mockito.mock(UserDAO.class), authProviderList, "dummy-auth-provider");
        assertThrows(IllegalStateException.class, () -> service.init());
    }

    /**
     * check, that a valid login works.
     */
    @Test
    void testLogin() {
        UserDAO userDAO = Mockito.mock(UserDAO.class);
        UserEntity user = new UserEntity("test", "test", "test");
        String encodedPassword = passwordEncoder.encode("test1234");
        user.setPassword(encodedPassword);
        user.forceID(2);
        when(userDAO.findOneByUsername(anyString())).thenReturn(Optional.of(user));

        assertTrue(passwordEncoder.matches( "test1234", encodedPassword));
        List<AuthProvider> authProviderList = new ArrayList<>();
        authProviderList.add(new DummyAuthProvider());

        AccountService service = new AccountService(userDAO, authProviderList, "dummy-auth-provider");
        service.init();
        Optional<AccountDTO> accountDTO = service.loginUser("test", "test1234");
        assertTrue(accountDTO.isPresent());
        assertEquals(2, accountDTO.get().getUserID());
    }

    @Test
    void testWrongLogin() {
        UserDAO userDAO = Mockito.mock(UserDAO.class);
        UserEntity user = new UserEntity("test", "test", "test");
        String encodedPassword = passwordEncoder.encode("test5678");
        user.setPassword(encodedPassword);
        when(userDAO.findOneByUsername(anyString())).thenReturn(Optional.of(user));

        assertFalse(passwordEncoder.matches( "test1234", encodedPassword));

        List<AuthProvider> authProviderList = new ArrayList<>();
        AccountService service = new AccountService(userDAO, authProviderList, "dummy-auth-provider");
        Optional<AccountDTO> accountDTO = service.loginUser("test", "test1234");
        assertTrue(accountDTO.isEmpty());
    }

}
