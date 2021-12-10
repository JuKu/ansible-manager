package com.jukusoft.anman.base.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * tests the functionality of the ImportUtils class, which checks some system property flags.
 *
 * @author Justin Kuenzel
 */
class ImportUtilsTest {

	@Test
	void testIsInitialImportEnabled() {
		//first, check for the absence of the system property
		System.clearProperty("initial.import.enabled");

		assertTrue(ImportUtils.isInitialImportEnabled());

		//now set the system property to false
		System.setProperty("initial.import.enabled", "false");
		assertFalse(ImportUtils.isInitialImportEnabled());

		//check also true value
		System.setProperty("initial.import.enabled", "true");
		assertTrue(ImportUtils.isInitialImportEnabled());
	}

}
