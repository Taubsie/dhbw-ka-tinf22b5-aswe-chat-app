package de.dhbw.ka.tinf22b5.terminal;

import java.awt.*;

public interface TerminalHandler {

    int BUFFER_SIZE = 32;

    void init() throws TerminalHandlerException;
    void deinit() throws TerminalHandlerException;

    byte[] getChar();

    Dimension getSize();

    default void attachShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.deinit();
            } catch (TerminalHandlerException _) {
            }
        }));
    }
}