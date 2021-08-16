package com.jukusoft.anman.base.config;

import org.junit.jupiter.api.Test;
import org.springframework.ldap.core.AuthenticationSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 * test class for AuthenticationSourceAdapter.
 *
 * @author Justin Kuenzel
 */
class AuthenticationSourceAdapterTest {

	/**
	 * check the getter methods that the return the same string as in parameters.
	 */
	@Test
	void testGetter() {
		AuthenticationSource adapter = new AuthenticationSourceAdapter("test1", "test2");
		assertEquals("test1", adapter.getPrincipal());
		assertEquals("test2", adapter.getCredentials());
	}

}
