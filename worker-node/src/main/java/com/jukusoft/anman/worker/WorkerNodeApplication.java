package com.jukusoft.anman.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * main class.
 *
 * @author Justin Kuenzel
 */
@SpringBootApplication(scanBasePackages = "com.jukusoft.anman")
@PropertySource({"classpath:base.yml"})
@Configuration
@ComponentScan
@EnableAutoConfiguration
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
