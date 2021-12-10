package com.jukusoft.anman.base.settings;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This test checks the {@link GlobalSettingsImporter}.
 *
 * @author Justin Kuenzel
 */
public class GlobalSettingsImporterTest {

	@BeforeAll
	public static void beforeClass() {
		GSUtils.cleanUp();
	}

	@AfterAll
	public static void afterClass() {
		GSUtils.cleanUp();
	}

	@Test
	void testConstructor() {
		GlobalSettingsImporter importer = new GlobalSettingsImporter(GSUtils.createGSService(false), GSUtils.createDAOMock(false));
		assertNotNull(importer);
	}

	@Test
	void testImport() throws Exception {
		GlobalSettingsImporter importer = new GlobalSettingsImporter(GSUtils.createGSService(false), GSUtils.createDAOMock(false));
		importer.afterPropertiesSet();
	}

}
