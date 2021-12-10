package com.jukusoft.anman.base.settings;

import com.jukusoft.anman.base.utils.ImportUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
	void testSkipImport() throws Exception {
		ImportUtils.setInitialImportEnabled(false);

		//create a new global settings service without state
		GlobalSettingsService gsService = GSUtils.createGSService(true);

		//first check, that the database is empty
		assertEquals(0, gsService.listSettings().size());

		//execute the importer
		GlobalSettingsImporter importer = new GlobalSettingsImporter(GSUtils.createGSService(false), GSUtils.createDAOMock(false));
		importer.afterPropertiesSet();

		//if the importer was skipped, there should be no setting in the database
		assertEquals(0, GSUtils.createGSService(false).listSettings().size());

		//try the same thing with initial import enabled, but settings import disabled
		ImportUtils.setInitialImportEnabled(true);
		ImportUtils.setInitialSettingsImportEnabled(false);

		importer.afterPropertiesSet();

		//if the importer was skipped, there should be no setting in the database
		assertEquals(0, GSUtils.createGSService(false).listSettings().size());
	}

	@Test
	void testImport() throws Exception {
		ImportUtils.setInitialImportEnabled(true);
		ImportUtils.setInitialSettingsImportEnabled(true);

		assertEquals(0, GSUtils.createGSService(false).listSettings().size());

		GlobalSettingsImporter importer = new GlobalSettingsImporter(GSUtils.createGSService(false), GSUtils.createDAOMock(false));
		importer.afterPropertiesSet();

		assertEquals(3, GSUtils.createGSService(false).listSettings().size());
	}

}
