package de.dhbw.ka.tinf22b5.dialog;

import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalRenderingBuffer;

import java.io.IOException;

public class WelcomeDialog extends Dialog {

    @Override
    public void render(TerminalRenderingBuffer terminalRenderingBuffer) {
        terminalRenderingBuffer.setCursurVisible(false);
        terminalRenderingBuffer.addString("Welcome to the Chat App!");
        terminalRenderingBuffer.nextLine();
        terminalRenderingBuffer.nextLine();
        terminalRenderingBuffer.addString("Press any of the following:");
        terminalRenderingBuffer.nextLine();
        terminalRenderingBuffer.addString("E - Start | C - Config | Q - Quit");
        terminalRenderingBuffer.nextLine();
    }

    @Override
    public void handleInput(TerminalKeyEvent event, TerminalHandler terminal) throws IOException {
        switch (event.getTerminalKey()) {
            case TerminalKey.TK_C, TerminalKey.TK_c -> terminal.changeDialog(new ConfigDialog());
            default -> super.handleInput(event, terminal);
        }
    }
}