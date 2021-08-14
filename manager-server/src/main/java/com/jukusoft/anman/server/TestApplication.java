package com.jukusoft.anman.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/*
 * a test application for junit tests.
 *
 * @author Justin Kuenzel
 */
@SpringBootApplication(scanBasePackages = {"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
@PropertySource({"classpath:base.properties"})
@EntityScan({"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
@EnableJpaRepositories({"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
public class TestApplication {

	//

}
