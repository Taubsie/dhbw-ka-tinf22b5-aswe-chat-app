package de.dhbw.ka.tinf22b5.terminal.key;

import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;

import java.io.IOException;

public interface TerminalKeyListener {
    void keyPressed(TerminalHandler terminal, TerminalKeyEvent terminalKeyEvent) throws IOException;
}
