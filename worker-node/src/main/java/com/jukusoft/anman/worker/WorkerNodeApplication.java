package com.jukusoft.anman.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * main class.
 *
 * @author Justin Kuenzel
 */
@SpringBootApplication
public class WorkerNodeApplication {

    /**
     * main method.
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(WorkerNodeApplication.class, args);
    }

}
