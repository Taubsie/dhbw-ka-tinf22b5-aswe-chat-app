package de.dhbw.ka.tinf22b5.util;

public class OSUtil {

    public enum OS {
        WIN,
        LINUX,
        MAC,
        OTHER
    }

    public static OS getOS() {
        String osName = System.getProperty("os.name", "other").toLowerCase();
        if (osName.contains("mac") || osName.contains("darwin")) {
            return OS.MAC;
        } else if (osName.contains("windows")) {
            return OS.WIN;
        } else if (osName.contains("nux") || osName.contains("nix")) {
            return OS.LINUX;
        } else {
            return OS.OTHER;
        }
    }
}
