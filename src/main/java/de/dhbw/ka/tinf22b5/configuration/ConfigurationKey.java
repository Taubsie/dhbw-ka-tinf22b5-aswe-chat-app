package de.dhbw.ka.tinf22b5.configuration;

import java.util.Arrays;
import java.util.Optional;

public enum ConfigurationKey {
    NETWORK_INTERFACE("network_interface", "Network device"),
    USERNAME("username", "Username"),
    BROADCAST_IP_ADDRESS("broadcast_ip_address", "Broadcast ip address"),
    BROADCAST_PORT("broadcast_port", "Broadcast port");

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
