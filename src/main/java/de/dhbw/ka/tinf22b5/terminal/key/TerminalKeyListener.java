package de.dhbw.ka.tinf22b5.terminal.key;

import java.io.IOException;

public interface TerminalKeyListener {
    void keyPressed(TerminalKeyEvent terminalKeyEvent) throws IOException;
}
