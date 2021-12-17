package com.jukusoft.anman.server.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * swagger configuration.
 *
 * @author Justin Kuenzel
 */
@Configuration
//@EnableSwagger2
@Profile("default")
public class Swagger2Config {

	/**
	 * the swagger ui title.
	 */
	@Value("${swagger.title}")
	private String title;

	/**
	 * the swagger ui description.
	 */
	@Value("${swagger.description}")
	private String description;

	/**
	 * the swagger ui contact name.
	 */
	@Value("${swagger.contact.name}")
	private String contactName;

	/**
	 * the swagger ui contact url.
	 */
	@Value("${swagger.contact.url}")
	private String contactUrl;

	/**
	 * the swagger ui contact mail.
	 */
	@Value("${swagger.contact.mail}")
	private String contactMail;

	/**
	 * the swagger ui license name.
	 */
	@Value("${swagger.license.name}")
	private String licenseName;

	/**
	 * the swagger ui license url.
	 */
	@Value("${swagger.license.url}")
	private String licenseUrl;

	/**
	 * the swagger api version.
	 */
	@Value("${swagger.version}")
	private String version;

	/**
	 * create and configure the api endpoints information.
	 *
	 * @return api endpoint information
	 */
	@Bean
	public OpenAPI anmanOpenAPI() {
		Contact contact = new Contact();
		contact.setName(contactName);
		contact.setUrl(contactUrl);
		contact.setEmail(contactMail);

		return new OpenAPI()
				.info(new Info().title(title)
						.description(description)
						.version(version)
						.license(new License().name(licenseName).url(licenseUrl))
						.contact(contact))
				.externalDocs(new ExternalDocumentation()
						.description(contactName)
						.url(contactUrl));
	}

}
