package de.dhbw.ka.tinf22b5.configuration;

import java.nio.file.Path;

public abstract class FileStorage {
    protected abstract String getDirectoryName();
    protected abstract String getFileName();

    protected Path getDirectoryPath() {
        return Path.of(System.getProperty("user.home"), ".aswe-chat-app", getDirectoryName());
    }

    protected Path getFilePath() {
        return getDirectoryPath().resolve(getFileName());
    }
}