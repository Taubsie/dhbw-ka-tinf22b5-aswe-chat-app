package de.dhbw.ka.tinf22b5.configuration;

import java.util.Optional;

public interface ConfigurationRepository {
    Optional<String> getConfigurationValue(ConfigurationKey key);

    void loadConfiguration();

    void setConfigurationValue(ConfigurationKey key, String value);

    void saveConfiguration();
}
