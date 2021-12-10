package com.jukusoft.anman.base.settings;

import com.jukusoft.anman.base.dao.GlobalSettingDAO;
import com.jukusoft.anman.base.entity.settings.GlobalSettingEntity;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * This utility class provides some mocks for the testing of the global settings classes.
 *
 * @author Justin Kuenzel
 */
public class GSUtils {

	private static GlobalSettingDAO dao;

	private GSUtils() {
		//
	}

	/**
	 * create a {@link GlobalSettingsService} instance with mocked DAO.
	 *
	 * @return GlobalSettingsService instance with mocked DAO
	 */
	public static GlobalSettingsService createGSService(boolean newDao) {
		return new GlobalSettingsService(createDAOMock(newDao));
	}

	public static GlobalSettingDAO createDAOMock(boolean newDao) {
		if (dao != null && !newDao) {
			return dao;
		}

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

		dao = daoMock;
		return daoMock;
	}

	public static void cleanUp() {
		dao = null;
	}

}
