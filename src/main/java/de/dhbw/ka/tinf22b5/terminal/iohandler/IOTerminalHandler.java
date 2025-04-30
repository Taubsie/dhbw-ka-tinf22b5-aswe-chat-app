package de.dhbw.ka.tinf22b5.terminal.iohandler;

import de.dhbw.ka.tinf22b5.terminal.exception.TerminalHandlerException;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public abstract class IOTerminalHandler {

    protected static final int BUFFER_SIZE = 32;

    protected final Set<ConsoleResizeListener> resizeListeners = new HashSet<ConsoleResizeListener>();

    protected void reportResize() {
        Set<ConsoleResizeListener> copy;
        synchronized (resizeListeners) {
            copy = new HashSet<>(resizeListeners);
        }

            copy.forEach(ConsoleResizeListener::onResize);
    }

    public void addResizeListener(ConsoleResizeListener listener) {
        synchronized (resizeListeners) {
            resizeListeners.add(listener);
        }
    }

    public abstract void init() throws TerminalHandlerException;
    public abstract void deinit() throws TerminalHandlerException;

    public abstract byte[] getChar();

    public abstract Dimension getSize();

    public void attachShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.deinit();
            } catch (TerminalHandlerException _) {
            }
        }));
    }
}