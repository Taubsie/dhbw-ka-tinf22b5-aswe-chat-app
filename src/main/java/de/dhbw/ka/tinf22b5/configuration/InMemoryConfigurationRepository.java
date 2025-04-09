package de.dhbw.ka.tinf22b5.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryConfigurationRepository implements ConfigurationRepository {

    private final Map<ConfigurationKey, Object> configurations;

    public InMemoryConfigurationRepository() {
        this.configurations = new HashMap<>();
    }

    @Override
    public Optional<String> getConfigurationValue(ConfigurationKey key) {
        return Optional.ofNullable((String)configurations.get(key));
    }

    @Override
    public Optional<Integer> getIntConfigurationValue(ConfigurationKey key) {
        return Optional.ofNullable((Integer) configurations.get(key));
    }

    @Override
    public void loadConfiguration() {
    }

    @Override
    public void setConfigurationValue(ConfigurationKey key, String value) {
        configurations.put(key, value);
    }

    @Override
    public void setIntConfigurationValue(ConfigurationKey key, int value) {
        configurations.put(key, value);
    }

    @Override
    public void removeConfigurationValue(ConfigurationKey key) {
        configurations.remove(key);
    }

    @Override
    public void saveConfiguration() {
    }
}
