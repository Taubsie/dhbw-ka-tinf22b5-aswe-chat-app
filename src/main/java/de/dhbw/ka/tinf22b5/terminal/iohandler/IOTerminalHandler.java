package de.dhbw.ka.tinf22b5.terminal.iohandler;

import de.dhbw.ka.tinf22b5.terminal.exception.TerminalHandlerException;
import de.dhbw.ka.tinf22b5.terminal.iohandler.lin.LinuxTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.iohandler.win.WindowsTerminalHandler;
import de.dhbw.ka.tinf22b5.util.OSUtil;

import java.awt.*;

public interface IOTerminalHandler {

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