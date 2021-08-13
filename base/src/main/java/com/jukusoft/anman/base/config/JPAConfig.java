package com.jukusoft.anman.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * the java persistence api configuration for hibernate.
 *
 * @author Justin Kuenzel
 */
@Configuration
@EnableTransactionManagement
@Profile("default")
public class JPAConfig {

    protected static final Logger logger = LoggerFactory.getLogger(JPAConfig.class);

    @Value("${database.type}")
    private String type;

    @Value("${database.ip}")
    private String ip;

    @Value("${database.port}")
    private int port;

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Value("${database.name}")
    private String dbName;

    @Value("${database.driver.class.name}")
    private String driverClassName;

    @Value("${database.sqlite.file}")
    private String sqliteFile;

    @Value("${hibernate.hbm2ddl.auto}")
    private String hibernateHbm2ddlAuto;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    /**
     * create the entity manager factory with its config.
     *
     * @return entity manager factory bean
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.jukusoft.anman", "com.jukusoft.authentification.jwt");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    /**
     * configure the datasource.
     *
     * @return database datasource
     */
    @Bean
    public DataSource dataSource() {
        if (type.toLowerCase().equals("h2")) {
            logger.info("load h2 database...");

            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
            dataSource.setUsername("sa");
            dataSource.setPassword("sa");
            return dataSource;
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);

        if (type.equals("sqlite")) {
            dataSource.setUrl("jdbc:sqlite:" + sqliteFile + "/" + dbName);
        } else {
            //?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
            dataSource.setUrl("jdbc:" + type + "://" + ip + ":" + port + "/" + dbName + "?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        }

        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    /**
     * create and configure transaction manager.
     *
     * @param emf entity manager factory
     *
     * @return transaction manager
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    /**
     * exception handling for transactions.
     *
     * @return exception processor
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    /**
     * get additional properties.
     *
     * @return additional properties
     */
    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
        properties.setProperty("hibernate.dialect", hibernateDialect);

        //avoid this exception: java.sql.SQLFeatureNotSupportedException: Die Methode org.postgresql.jdbc4.Jdbc4Connection.createClob() ist noch nicht implementiert.
        properties.setProperty("spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation", "true");

        return properties;
    }

}
