package com.jukusoft.anman.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * a spring condition which is used to enable the swagger only, if the property is set to true.
 *
 * @author Justin Kuenzel
 */
public class SwaggerCondition implements Condition {

    /**
     * configuration value, if swagger is enabled.
     */
    @Value("${swagger.enabled}")
    private boolean swaggerEnabled = true;

    /**
     * check, if the condition is true.
     *
     * @param conditionContext condition context
     * @param annotatedTypeMetadata annotated type metatdata
     *
     * @return true, if the condition is matched
     */
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return swaggerEnabled;
    }

}
