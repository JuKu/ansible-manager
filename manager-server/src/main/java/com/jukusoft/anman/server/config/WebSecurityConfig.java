package com.jukusoft.anman.server.config;

import com.jukusoft.authentification.jwt.config.JWTWebSecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * the web security configuration.
 *
 * @author Justin Kuenzel
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableWebSecurity
/*@EnableGlobalMethodSecurity(
        prePostEnabled = true)*/
public class WebSecurityConfig extends JWTWebSecurityConfig {

    /**
     * get a list with all permitted pages (without login).
     *
     * @return list with all permitted pages
     */
    @Override
    protected String[] listPermittedPages() {
        return new String[]{"/", "/api/register", "/swagger", "/swagger-ui", "/swagger-ui.html", "/h2/**", "/swagger/**", "/swagger-*", "/swagger-ui/**", "/swagger-resources/**", "/csrf", "/v2/**", "/v3/**", "/webjars/**", "/actuator", "/actuator/*", "/errors/*", "/error", "/pages/*", "/res/**"};
    }

}
