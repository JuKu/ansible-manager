package com.jukusoft.anman.base.utils;

public class ImportUtils {

    private ImportUtils() {
        //
    }

    public static boolean isInitialImportEnabled() {
        //TODO: use spring @Value instead
        return Boolean.parseBoolean(System.getProperty("initial.import.enabled", "true"));
    }

	public static void setInitialImportEnabled(boolean enabled) {
		System.setProperty("initial.import.enabled", Boolean.toString(enabled));
	}

}
