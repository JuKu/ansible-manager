package com.jukusoft.anman.base.settings;

import com.jukusoft.anman.base.dao.GlobalSettingDAO;
import com.jukusoft.anman.base.entity.settings.GlobalSettingEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * tests the GlobalSettingsService.
 *
 * @author Justin Kuenzel
 */
class GlobalSettingsServiceTest {

	@BeforeAll
	public static void beforeClass() {
		GSUtils.cleanUp();
	}

	@AfterAll
	public static void afterClass() {
		GSUtils.cleanUp();
	}

	/**
	 * the method checks, that the constructor does not throws an exception.
	 */
	@Test
	void testConstructor() {
		GlobalSettingsService service = new GlobalSettingsService(Mockito.mock(GlobalSettingDAO.class));
		assertNotNull(service);
	}

	@Test
	void testGetNotExistentSetting() {
		GlobalSettingsService gsService = createGSService();
		Optional<SettingDTO> opt = gsService.getSetting("not-existent-setting-key");
		assertTrue(opt.isEmpty());

		//create the setting
		gsService.putSetting("not-existent-setting-key", "test");
		opt = gsService.getSetting("not-existent-setting-key");
		assertTrue(opt.isPresent());
		assertEquals("test", opt.get().getValue());
	}

	@Test
	void testListSettings() {
		GlobalSettingsService gsService = createGSService();
		assertTrue(gsService.listSettings().isEmpty());

		//add some settings
		gsService.addSetting("test1", "test", "test1");
		gsService.addSetting("test2", "test", "test2");
		gsService.addSetting("test3", "test", "test3");

		assertFalse(gsService.listSettings().isEmpty());
		assertEquals(3, gsService.listSettings().size());

		//the order of the map is not guaranteed, so we have to check for keys here
		List<String> keys = gsService.listSettings().stream().map(entity -> entity.getKey()).toList();
		assertTrue(keys.contains("test1"));
		assertTrue(keys.contains("test2"));
		assertTrue(keys.contains("test3"));

		gsService.clearCache("test1");
		gsService.clearCache();
	}

	@Test
	void testAddBlankKeySetting() {
		GlobalSettingsService gsService = createGSService();
		assertTrue(gsService.listSettings().isEmpty());

		//add some settings
		assertThrows(IllegalArgumentException.class, () -> gsService.addSetting("", "test-value-1", "test1"));
	}

	@Test
	void testUpdateSetting() {
		GlobalSettingsService gsService = createGSService();
		assertTrue(gsService.listSettings().isEmpty());

		//add some settings
		gsService.addSetting("test1", "test-value-1", "test1");
		gsService.addSetting("test2", "test-value-2", "test2");
		gsService.addSetting("test3", "test-value-3", "test3");

		assertEquals("test-value-1", gsService.getSetting("test1").get().getValue());

		//update setting value
		gsService.putSetting("test1", "new-value-1");
		assertEquals("new-value-1", gsService.getSetting("test1").get().getValue());
	}

	/**
	 * create a {@link GlobalSettingsService} instance with mocked DAO.
	 *
	 * @return GlobalSettingsService instance with mocked DAO
	 */
	protected GlobalSettingsService createGSService() {
		return GSUtils.createGSService(true);
	}

}
