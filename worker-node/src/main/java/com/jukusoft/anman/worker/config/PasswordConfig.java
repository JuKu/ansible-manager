package com.jukusoft.anman.worker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * a configuration bean to create the password encoder (because worker node does not have spring web mvc with its security configuration as the manager).
 *
 * @author Justin Kuenzel
 */
@Configuration
public class PasswordConfig {

    /**
     * create the password encoder.
     *
     * @return password encoder
     */
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
