package de.dhbw.ka.tinf22b5.terminal.render.dialog;

import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalScreen;
import de.dhbw.ka.tinf22b5.terminal.render.characters.TerminalCharacterFactory;

import java.io.IOException;

public class WelcomeDialog extends Dialog {

    @Override
    public void render(TerminalScreen terminalScreen) {
        terminalScreen.setCursorPosition(0, 0);
        terminalScreen.setCharacters(TerminalCharacterFactory.createTerminalCharactersFromString("Welcome to the Chat App!"));
        terminalScreen.setCursorPosition(0, 2);
        terminalScreen.setCharacters(TerminalCharacterFactory.createTerminalCharactersFromString("Press any of the following:"));
        terminalScreen.setCursorPosition(0, 3);
        terminalScreen.setCharacters(TerminalCharacterFactory.createTerminalCharactersFromString("E - Start | C - Config | Q - Quit"));
    }

    @Override
    public void handleInput(TerminalHandler terminal, TerminalKeyEvent event) throws IOException {
        switch (event.getTerminalKey()) {
            case TerminalKey.TK_C, TerminalKey.TK_c -> terminal.changeDialog(new ConfigDialog());
            default -> super.handleInput(terminal, event);
        }
    }
}