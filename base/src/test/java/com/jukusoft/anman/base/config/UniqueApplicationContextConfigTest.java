package com.jukusoft.anman.base.config;

import com.jukusoft.anman.base.security.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * test class for UniqueApplicationContextConfig, which ensures, that there is only one unique application context for the whole application.
 *
 * @author Justin Kuenzel
 */
class UniqueApplicationContextConfigTest {

    /**
     * test the getter and setter to ensure, that the return always the same value.
     */
    @Test
    void testSetterAndGetter() {
        UniqueApplicationContextConfig config = new UniqueApplicationContextConfig();
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        config.setApplicationContext(context);

        assertNotNull(UniqueApplicationContextConfig.getContext());
        assertEquals(context, UniqueApplicationContextConfig.getContext());

        //test, if the next call of getContext() returns the same context
        assertEquals(context, UniqueApplicationContextConfig.getContext());

        assertNotNull(config.buildProperties());
        assertNotNull(config.taskExecutor());
        assertNotNull(config.taskScheduler());
    }

    /**
     * test, that requesting a known bean
     * @throws ClassNotFoundException
     */
    @Test
    void testGetBean() throws ClassNotFoundException {
        UniqueApplicationContextConfig config = new UniqueApplicationContextConfig();
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        config.setApplicationContext(context);

        UniqueApplicationContextConfig.getBean(String.class);
    }

    /**
     * test, that requesting an unknwon bean returns an exception.
     *
     * @throws ClassNotFoundException
     */
    @Test
    void testGetUnknownBean() throws ClassNotFoundException {
        UniqueApplicationContextConfig config = new UniqueApplicationContextConfig();
        ApplicationContext context = Mockito.mock(ApplicationContext.class);
        when(context.getBean(any(Class.class))).thenThrow(new NoSuchBeanDefinitionException(""));
        config.setApplicationContext(context);

        assertThrows(NoSuchBeanDefinitionException.class, () -> UniqueApplicationContextConfig.getBean(AccountService.class));
    }

}
