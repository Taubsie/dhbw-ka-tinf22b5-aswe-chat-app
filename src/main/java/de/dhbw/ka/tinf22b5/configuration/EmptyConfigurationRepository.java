package de.dhbw.ka.tinf22b5.configuration;

import java.util.Optional;

public class EmptyConfigurationRepository implements ConfigurationRepository {

    @Override
    public Optional<String> getConfigurationValue(ConfigurationKey key) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getIntConfigurationValue(ConfigurationKey key) {
        return Optional.empty();
    }

    @Override
    public void setConfigurationValue(ConfigurationKey key, String value) {
    }

    @Override
    public void setIntConfigurationValue(ConfigurationKey key, int value) {
    }

    @Override
    public void removeConfigurationValue(ConfigurationKey key) {
    }
}
