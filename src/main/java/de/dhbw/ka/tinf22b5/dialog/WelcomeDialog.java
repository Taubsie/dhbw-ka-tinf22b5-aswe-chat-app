package de.dhbw.ka.tinf22b5.dialog;

import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;

import java.io.IOException;

public class WelcomeDialog extends Dialog {
    @Override
    public void print() {
        System.out.println("Welcome to the Chat App!");
        System.out.println();
        System.out.println("Press any of the following:");
        System.out.println("E - Start | C - Config | Q - Quit");
    }

    @Override
    public void handleInput(TerminalKeyEvent event, TerminalHandler terminal) throws IOException {
        switch (event.getTerminalKey()) {
            case TerminalKey.TK_C, TerminalKey.TK_c -> terminal.changeDialog(new ConfigDialog());
            default -> super.handleInput(event, terminal);
        }
    }
}