package com.jukusoft.anman.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * junit tests for main class.
 *
 * @author Justin Kuenzel
 */
@SpringBootTest
public class AnManagerBackendApplicationTest {

    @Value("${test.property}")
    private String testProperty;

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

}
