package de.dhbw.ka.tinf22b5.terminal;

import java.awt.*;

public interface TerminalHandler {

    void init() throws TerminalHandlerException;
    int getChar();
    Dimension getSize();
    void deinit();
}