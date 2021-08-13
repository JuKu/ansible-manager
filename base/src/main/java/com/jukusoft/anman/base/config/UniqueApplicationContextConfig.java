package com.jukusoft.anman.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * a unique application context config, so that only one application context exists.
 *
 * see also: https://stackoverflow.com/questions/30431776/using-scheduled-and-enablescheduling-but-gives-nosuchbeandefinitionexception/48861718
 */
@EnableAsync
@Configuration
public class UniqueApplicationContextConfig implements ApplicationContextAware {

    /**
     * the logger.
     */
    private static Logger logger = LoggerFactory.getLogger(UniqueApplicationContextConfig.class);

    /**
     * the singleton unique application context.
     */
    private static org.springframework.context.ApplicationContext applicationContext = null;

    /**
     * get the current application context.
     *
     * @return application context
     */
    public static org.springframework.context.ApplicationContext getContext() {
        return applicationContext;
    }

    /**
     * set application context.
     *
     * @param context application context
     */
    @Override
    public synchronized void setApplicationContext(final org.springframework.context.ApplicationContext context) {
        applicationContext = context;
    }

    public static <T> T getBean(Class<T> cls) throws ClassNotFoundException {
        return applicationContext.getBean(cls);
    }

    /**
     * see also: https://stackoverflow.com/questions/30431776/using-scheduled-and-enablescheduling-but-gives-nosuchbeandefinitionexception/48861718
     * <p>
     * https://docs.spring.io/spring/docs/3.2.x/spring-framework-reference/html/scheduling.html
     */
    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    /**
     * see also: https://stackoverflow.com/questions/30431776/using-scheduled-and-enablescheduling-but-gives-nosuchbeandefinitionexception/48861718
     */
    @Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    @ConditionalOnMissingBean(BuildProperties.class)
    public BuildProperties buildProperties() {
        logger.warn("BuildProperties bean did not auto-load, creating mock BuildProperties");
        return new BuildProperties(new Properties());
    }

}
