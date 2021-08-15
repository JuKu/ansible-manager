package com.jukusoft.anman.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
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
import java.util.Objects;
import java.util.Properties;

/**
 * configuration for h2 database (for junit tests only).
 *
 * @author Justin Kuenzel
 */
@Configuration
@EnableTransactionManagement
@Profile("test")
@PropertySource({"classpath:db-test.properties"})
public class TestH2Config {

    /**
     * the logger.
     */
    protected static final Logger logger = LoggerFactory.getLogger(TestH2Config.class);

    /**
     * hibernate auto configuration.
     */
    @Value("${hibernate.hbm2ddl.auto}")
    private String hibernateHbm2ddlAuto;

    /**
     * the database dialect for hibernate.
     */
    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    /**
     * create the entity manager factory.
     * 
     * @return entity manager factory
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
     * @return datasource
     */
    @Bean
    public DataSource dataSource() {
        logger.info("load h2 database...");

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    /**
     * create thr transaction manager.
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
        Objects.requireNonNull(hibernateHbm2ddlAuto);
        Objects.requireNonNull(hibernateDialect);

        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
        properties.setProperty("hibernate.dialect", hibernateDialect);

        //avoid this exception: java.sql.SQLFeatureNotSupportedException: Die Methode org.postgresql.jdbc4.Jdbc4Connection.createClob() ist noch nicht implementiert.
        //properties.setProperty("spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation", "true");

        return properties;
    }

}
