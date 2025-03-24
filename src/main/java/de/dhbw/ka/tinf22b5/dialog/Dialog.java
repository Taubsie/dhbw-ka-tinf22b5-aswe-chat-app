package de.dhbw.ka.tinf22b5.dialog;

import de.dhbw.ka.tinf22b5.terminal.*;
import de.dhbw.ka.tinf22b5.terminal.handler.TerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyListener;

import java.io.IOException;

public abstract class Dialog implements TerminalKeyListener {
    public abstract void print();

    public void handleInput(TerminalKeyEvent event, TerminalHandler terminal) throws IOException {
        switch (event.getTerminalKey()) {
            case TerminalKey.TK_UP, TerminalKey.TK_W, TerminalKey.TK_w -> terminal.moveCursor(CursorDirection.UP);
            case TerminalKey.TK_DOWN, TerminalKey.TK_S, TerminalKey.TK_s -> terminal.moveCursor(CursorDirection.DOWN);
            case TerminalKey.TK_RIGHT, TerminalKey.TK_D, TerminalKey.TK_d -> terminal.moveCursor(CursorDirection.RIGHT);
            case TerminalKey.TK_LEFT, TerminalKey.TK_A, TerminalKey.TK_a -> terminal.moveCursor(CursorDirection.LEFT);
            case TerminalKey.TK_q, TerminalKey.TK_Q -> terminal.quit();
        }
    }

    @Override
    public final void keyPressed(TerminalKeyEvent terminalKeyEvent) throws IOException {
        handleInput(terminalKeyEvent, terminalKeyEvent.getTerminalHandler());
    }
}
