package com.jukusoft.anman.base.utils;

public class ImportUtils {

    private ImportUtils() {
        //
    }

    public static boolean isInitialImportEnabled() {
        //TODO: use spring @Value instead
        return Boolean.parseBoolean(System.getProperty("initial.import.enabled", "true"));
    }

	public static boolean isInitialSettingsImportEnabled() {
		return Boolean.parseBoolean(System.getProperty("import.global.settings", "true"));
	}

	public static void setInitialImportEnabled(boolean enabled) {
		System.setProperty("initial.import.enabled", Boolean.toString(enabled));
	}

	public static void setInitialSettingsImportEnabled(boolean enabled) {
		System.setProperty("import.global.settings", Boolean.toString(enabled));
	}

}
