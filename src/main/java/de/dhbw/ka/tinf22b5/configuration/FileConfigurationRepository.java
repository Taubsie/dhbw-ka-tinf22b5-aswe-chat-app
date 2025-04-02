package de.dhbw.ka.tinf22b5.configuration;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class FileConfigurationRepository extends FileStorage implements ConfigurationRepository {
    private final Properties properties;

    public FileConfigurationRepository() {
        properties = new Properties();
        loadConfiguration();
    }

    @Override
    public String getConfigurationValue(ConfigurationKey key) {
        return properties.getProperty(key.getKey());
    }

    @Override
    public void loadConfiguration() {
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
        } catch (FileAlreadyExistsException ignored) {
            //ignored
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }

        try {
            properties.load(Files.newBufferedReader(filePath));
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
    public void saveConfiguration() {
        try {
            properties.store(Files.newBufferedWriter(getFilePath()), null);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    @Override
    public String getDirectoryName() {
        return "config";
    }

    @Override
    public String getFileName() {
        return "client.properties";
    }
}