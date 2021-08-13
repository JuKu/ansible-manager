package com.jukusoft.anman.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * main class.
 *
 * @author Justin Kuenzel
 */
@SpringBootApplication
public class AnsibleManagerBackendApplication {

    /**
     * main method.
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AnsibleManagerBackendApplication.class, args);
    }

}
