package com.jukusoft.anman.worker;

import com.jukusoft.anman.base.dao.RefreshDAOImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * main class.
 *
 * @author Justin Kuenzel
 */
@SpringBootApplication(scanBasePackages = {"com.jukusoft.anman", "com.jukusoft.authentification.jwt"}, exclude = {
        LdapAutoConfiguration.class
})
@PropertySource({"classpath:base.properties"})
//@Configuration
//@ComponentScan
@EnableCaching
@EnableScheduling
@EntityScan({"com.jukusoft.anman", "com.jukusoft.authentification.jwt"})
@EnableJpaRepositories(value = {"com.jukusoft.anman", "com.jukusoft.authentification.jwt"}, repositoryBaseClass = RefreshDAOImpl.class)
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
