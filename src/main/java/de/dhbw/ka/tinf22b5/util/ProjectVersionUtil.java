package de.dhbw.ka.tinf22b5.util;

import de.dhbw.ka.tinf22b5.Main;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProjectVersionUtil {
    private ProjectVersionUtil() {}

    @NotNull
    public static String getProjectVersion() {
        try(InputStream is = Main.class.getClassLoader().getResourceAsStream("config/version.properties")) {
            Properties properties = new Properties();
            properties.load(is);

            return properties.getProperty("version", "not set");
        }
        catch (IOException ignored) {
            return "unknown";
        }
    }
}