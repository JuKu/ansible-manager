package com.jukusoft.anman.base.version;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * This configuration class provides the bean with the version information.
 * See also: https://www.baeldung.com/spring-git-information .
 *
 * @author Justin Kuenzel
 */
@Configuration
public class VersionConfiguration {

	protected VersionConfiguration() {
		//
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
		PropertySourcesPlaceholderConfigurer propsConfig
				= new PropertySourcesPlaceholderConfigurer();
		propsConfig.setLocation(new ClassPathResource("git.properties"));
		propsConfig.setIgnoreResourceNotFound(false);
		propsConfig.setIgnoreUnresolvablePlaceholders(true);
		return propsConfig;
	}

}
