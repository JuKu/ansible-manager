package com.jukusoft.anman.base.settings;

public class SettingDTO {

	private String key;
	private String value;
	private String title;

	public SettingDTO(String key, String value, String title) {
		this.key = key;
		this.value = value;
		this.title = title;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String getTitle() {
		return title;
	}

}
