package com.jukusoft.anman.base.settings;

import com.google.common.io.Resources;
import com.jukusoft.anman.base.dao.GlobalSettingDAO;
import com.jukusoft.anman.base.entity.settings.GlobalSettingEntity;
import com.jukusoft.anman.base.utils.ImportUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * this importer imports global settings, if absent
 */
@Configuration
@Profile("default")
public class GlobalSettingsImporter implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(GlobalSettingsImporter.class);

	@Autowired
	private GlobalSettingsService settingsService;

	@Autowired
	private GlobalSettingDAO settingDAO;

	/**
	 * import default global settings, if absent
	 *
	 * @throws Exception
	 */
	@Bean(name = "global_settings_import")
	@Override
	public void afterPropertiesSet() throws Exception {
		if (!ImportUtils.isInitialImportEnabled()) {
			return;
		}

		if (Boolean.parseBoolean(System.getProperty("import.global.settings", "true"))) {
			logger.info("import global settings, if absent");

			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URL url = loader.getResource("settings/default_settings.json");
			String content = Resources.toString(url, StandardCharsets.UTF_8);

			JSONArray jsonArray = new JSONArray(content);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);

				String key = json.getString("key");
				String value = json.getString("value");
				String title = json.getString("title");

				//check existing settings
				Optional<GlobalSettingEntity> settingOptional = settingDAO.findById(key);

				if (settingOptional.isPresent()) {
					GlobalSettingEntity setting = settingOptional.get();

					//only update setting title
					setting.setTitle(title);
					settingDAO.save(setting);
				} else {
					//create new setting
					logger.info("create new global setting: {}", key);

					GlobalSettingEntity setting = new GlobalSettingEntity(key, value, title);
					settingDAO.save(setting);
				}
			}

			//clear cache
			settingsService.clearCache();

			logger.info("{} global settings found in database", settingDAO.count());
		} else {
			logger.info("skip import of default settings");
		}
	}

}
