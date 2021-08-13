package com.jukusoft.anman.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * main class.
 *
 * @author Justin Kuenzel
 */
@SpringBootApplication(scanBasePackages = {"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
@PropertySource({"classpath:base.properties"})
//@Configuration
//@ComponentScan
@EnableCaching
@EnableScheduling
@EntityScan({"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
@EnableJpaRepositories({"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
public class AnManagerBackendApplication {

    /**
     * main method.
     *
     * @param args program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AnManagerBackendApplication.class, args);
    }

}
