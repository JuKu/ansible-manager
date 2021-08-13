package com.jukusoft.anman.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger configuration.
 *
 * @author Justin Kuenzel
 */
@Configuration
@EnableSwagger2
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
     * create a swagger docket.
     *
     * @return swagger docket
     */
    @Bean
    @Conditional(SwaggerCondition.class)
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.jukusoft"))
                .paths(PathSelectors.any())//PathSelectors.regex("/.*")
                .build().apiInfo(apiEndPointsInfo());
    }

    /**
     * create and configure the api endpoints information.
     *
     * @return api endpoint information
     */
    private ApiInfo apiEndPointsInfo() {
        return new ApiInfoBuilder().title(title)
                .description(description)
                .contact(new Contact(contactName, contactUrl, contactMail))
                .license(licenseName)
                .licenseUrl(licenseUrl)
                .version(version)
                .build();
    }

}
