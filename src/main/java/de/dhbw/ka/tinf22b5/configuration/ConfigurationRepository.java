package de.dhbw.ka.tinf22b5.configuration;

import java.util.Optional;

public interface ConfigurationRepository {
    Optional<String> getConfigurationValue(ConfigurationKey key);
    Optional<Integer> getIntConfigurationValue(ConfigurationKey key);

    void setConfigurationValue(ConfigurationKey key, String value);
    void setIntConfigurationValue(ConfigurationKey key, int value);
    void removeConfigurationValue(ConfigurationKey key);
}
