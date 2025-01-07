package de.dhbw.ka.tinf22b5;

import java.awt.*;

public interface TerminalHandler {

    void init();
    int getChar();
    Dimension getSize();
    void deinit();
}