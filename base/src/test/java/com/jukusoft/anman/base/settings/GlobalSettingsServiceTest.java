package com.jukusoft.anman.base.settings;

import com.jukusoft.anman.base.dao.GlobalSettingDAO;
import com.jukusoft.anman.base.entity.settings.GlobalSettingEntity;
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

	/**
	 * create a {@link GlobalSettingsService} instance with mocked DAO.
	 *
	 * @return GlobalSettingsService instance with mocked DAO
	 */
	protected GlobalSettingsService createGSService() {
		Map<String, GlobalSettingEntity> emulatedSettings = new HashMap();
		GlobalSettingDAO daoMock = Mockito.mock(GlobalSettingDAO.class);
		Mockito.when(daoMock.save(any(GlobalSettingEntity.class)))
				.thenAnswer((Answer<GlobalSettingEntity>) invocation -> {
							GlobalSettingEntity entity = invocation.getArgument(0);
							emulatedSettings.put(entity.getKey(), entity);

							return entity;
						}
				);
		Mockito.when(daoMock.findById(anyString()))
				.thenAnswer(entity -> Optional.ofNullable(emulatedSettings.get(entity.getArgument(0))));
		Mockito.when(daoMock.findAll())
				.thenReturn(emulatedSettings.values());

		return new GlobalSettingsService(daoMock);
	}

}
