package de.dhbw.ka.tinf22b5.configuration;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

public class FileConfigurationRepository extends FileStorage implements ConfigurationRepository {
    private final Properties properties;

    public FileConfigurationRepository() {
        properties = new Properties();
        loadConfiguration();
    }

    @Override
    public Optional<String> getConfigurationValue(ConfigurationKey key) {
        return Optional.ofNullable(properties.getProperty(key.getKey()));
    }

    @Override
    public Optional<Integer> getIntConfigurationValue(ConfigurationKey key) {
        return getConfigurationValue(key).map((v) -> {
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException ignored) {
                return null;
            }
        });
    }

    private void loadConfiguration() {
        boolean init = false;
        Path directoryPath = getDirectoryPath();

        try {
            Files.createDirectories(directoryPath);
        } catch (FileAlreadyExistsException ignored) {
            //ignored
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        Path filePath = getFilePath();

        try {
            Files.createFile(filePath);
            init = true;
        } catch (FileAlreadyExistsException ignored) {
            //ignored
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        try {
            properties.load(Files.newBufferedReader(filePath));

            if (init) {
                if (getConfigurationValue(ConfigurationKey.USERNAME).isEmpty()) {
                    setConfigurationValue(ConfigurationKey.USERNAME, UUID.randomUUID().toString());
                }

                saveConfiguration();
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Override
    public void setConfigurationValue(ConfigurationKey key, String value) {
        properties.setProperty(key.getKey(), value);
        saveConfiguration();
    }

    @Override
    public void setIntConfigurationValue(ConfigurationKey key, int value) {
        setConfigurationValue(key, Integer.toString(value));
    }

    @Override
    public void removeConfigurationValue(ConfigurationKey key) {
        properties.remove(key.getKey());
        saveConfiguration();
    }

    private void saveConfiguration() {
        try {
            properties.store(Files.newBufferedWriter(getFilePath()), null);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Override
    protected String getDirectoryName() {
        return "config";
    }

    @Override
    protected String getFileName() {
        return "client.properties";
    }
}