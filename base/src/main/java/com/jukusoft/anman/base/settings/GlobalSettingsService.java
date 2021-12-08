package com.jukusoft.anman.base.settings;

import com.jukusoft.anman.base.dao.GlobalSettingDAO;
import com.jukusoft.anman.base.entity.settings.GlobalSettingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * the global setting service which is responsible for managing global settings.
 *
 * @author Justin Kuenzel
 */
@Service
public class GlobalSettingsService {

	private static Logger logger = LoggerFactory.getLogger(GlobalSettingsService.class);

	@Autowired
	private GlobalSettingDAO settingDAO;

	public List<SettingDTO> listSettings() {
		return StreamSupport.stream(settingDAO.findAll().spliterator(), false)
				.filter(Objects::nonNull)
				.map(settingEntity -> new SettingDTO(settingEntity.getKey(), settingEntity.getValue(), settingEntity.getTitle()))
				.toList();
	}

	@Cacheable(cacheNames = "global_settings", key = "'global_settings_'.concat(#key)")
	public Optional<SettingDTO> getSetting(String key) {
		GlobalSettingEntity setting = settingDAO.findById(key).orElse(null);

		return Stream.of(setting)
				.filter(Objects::nonNull)
				.map(settingEntity -> new SettingDTO(settingEntity.getKey(), settingEntity.getValue(), settingEntity.getTitle()))
				.findFirst();
	}

	@CacheEvict(cacheNames = "global_settings", key = "'global_settings_'.concat(#key)")
	public void putSetting(String key, String value) {
		Optional<GlobalSettingEntity> settingEntityOptional = settingDAO.findById(key);

		if (!settingEntityOptional.isPresent()) {
			//create new setting entity
			logger.info("create new setting");

			GlobalSettingEntity setting = new GlobalSettingEntity(key, value, key);
			settingDAO.save(setting);
		} else {
			GlobalSettingEntity setting = settingEntityOptional.get();

			setting.setValue(value);
			settingDAO.save(setting);
		}
	}

	@CacheEvict(cacheNames = "global_settings", key = "'global_settings_'.concat(#key)")
	public void clearCache(String key) {
		//
	}

	@CacheEvict(cacheNames = "global_settings")
	public void clearCache() {
		//
	}

}
