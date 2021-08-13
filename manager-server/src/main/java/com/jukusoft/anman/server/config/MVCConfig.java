package com.jukusoft.anman.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * the web mvc configuration.
 *
 * @author Justin Kuenzel
 */
@Configuration
@Profile("default")
public class MVCConfig implements WebMvcConfigurer {

    /**
     * add resource handlers (e.q. for static resources.
     *
     * @param registry resource handler registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //
    }

    /**
     * add interceptors.
     *
     * @param registry interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //add interceptors here, if neccessary
    }

}
