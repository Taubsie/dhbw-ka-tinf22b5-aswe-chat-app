package de.dhbw.ka.tinf22b5.configuration;

import java.util.Arrays;
import java.util.Optional;

public enum ConfigurationKey {
    NETWORK_INTERFACE("network_interface", "Network device"),
    USERNAME("username", "Username");

    private final String key;
    private final String displayName;

    ConfigurationKey(String key, String displayName) {
        this.key = key;
        this.displayName = displayName;
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<ConfigurationKey> getByOrdinal(int ordinal) {
        try {
            return Optional.ofNullable(Arrays.asList(values()).get(ordinal));
        } catch (IndexOutOfBoundsException ignored) {
            return Optional.empty();
        }
    }
}
