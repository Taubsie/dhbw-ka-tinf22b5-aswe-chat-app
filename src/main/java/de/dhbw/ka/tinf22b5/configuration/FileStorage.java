package de.dhbw.ka.tinf22b5.configuration;

import java.nio.file.Path;

public abstract class FileStorage {
    public abstract String getDirectoryName();
    public abstract String getFileName();

    public Path getDirectoryPath() {
        return Path.of(System.getProperty("user.home"), ".aswe-chat-app", getDirectoryName());
    }

    public Path getFilePath() {
        return getDirectoryPath().resolve(getFileName());
    }
}