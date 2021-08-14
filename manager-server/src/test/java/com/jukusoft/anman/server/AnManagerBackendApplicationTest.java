package com.jukusoft.anman.server;

import com.jukusoft.anman.base.security.AuthProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * junit tests for main class.
 *
 * @author Justin Kuenzel
 */
@SpringBootTest
@ActiveProfiles(profiles = {"test"})
public class AnManagerBackendApplicationTest {

    @Value("${test.property}")
    private String testProperty;

    @Autowired
    private Collection<AuthProvider> authProviders;

    /**
     * check, if the context can be created, this means the application can startup.ss
     */
    @Test
    void contextLoads() {
        //
    }

    /**
     * verify, that the base.properties file from the base-dependency can be loaded successfully.
     */
    @Test
    public void testBasePropertiesFileLoaded() {
        assertEquals("test", testProperty);
    }

    /**
     * this test verifies, that the classpath contains minimum one auth providers
     */
    @Test
    public void testThatClasspathContainsMinimumOneAuthProvider() {
        assertFalse(authProviders.isEmpty());
        assertTrue(authProviders.size() >= 1);
    }

}
