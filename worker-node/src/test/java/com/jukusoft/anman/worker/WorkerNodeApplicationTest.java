package com.jukusoft.anman.worker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * junit tests for main class.
 *
 * @author Justin Kuenzel
 */
@SpringBootTest
@ActiveProfiles(profiles = {"test"})
public class WorkerNodeApplicationTest {

    /**
     * check, if the context can be created, this means the application can startup.ss
     */
    @Test
    void contextLoads() {
        //
    }

}
