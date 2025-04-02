package de.dhbw.ka.tinf22b5.configuration;

public interface ConfigurationRepository {
    String getConfigurationValue(ConfigurationKey key);

    void loadConfiguration();

    void setConfigurationValue(ConfigurationKey key, String value);

    void saveConfiguration();
}
