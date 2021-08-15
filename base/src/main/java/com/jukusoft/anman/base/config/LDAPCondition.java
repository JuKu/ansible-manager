package com.jukusoft.anman.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/*
 *
 *
 * @author Justin Kuenzel
 */
public class LDAPCondition implements Condition {

	/**
	 * configuration value, if ldap connection is enabled.
	 */
	@Value("${ldap.enabled}")
	private boolean ldapEnabled = true;

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
		return ldapEnabled;
	}

}
