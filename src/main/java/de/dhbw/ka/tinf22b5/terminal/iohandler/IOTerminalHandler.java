package de.dhbw.ka.tinf22b5.terminal.iohandler;

import de.dhbw.ka.tinf22b5.terminal.exception.TerminalHandlerException;

import java.awt.*;
import java.io.IOException;
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


    public byte[] getChar() {
        byte[] buffer = new byte[BUFFER_SIZE];

        int numRead = 0;
        try {
            numRead = System.in.read(buffer);
        } catch (IOException e) {
            return new byte[0];
        }

        // probably eof signal or kill
        if(numRead <= -1)
            return new byte[0];

        byte[] ret = new byte[numRead];
        System.arraycopy(buffer, 0, ret, 0, numRead);
        return ret;
    }

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