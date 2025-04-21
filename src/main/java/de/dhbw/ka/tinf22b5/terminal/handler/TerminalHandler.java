package de.dhbw.ka.tinf22b5.terminal.handler;

import de.dhbw.ka.tinf22b5.dialog.Dialog;
import de.dhbw.ka.tinf22b5.terminal.CursorDirection;

import java.io.IOException;

public interface TerminalHandler {

    int getCursorX();

    int getCursorY();

    void setCursorX(int x);

    void setCursorY(int y);

    void updateCursor(int x, int y);

    default void moveCursor(CursorDirection direction) {
        switch (direction) {
            case UP -> updateCursor(getCursorX(), getCursorY() - 1);
            case DOWN -> updateCursor(getCursorX(), getCursorY() + 1);
            case LEFT -> updateCursor(getCursorX() - 1, getCursorY());
            case RIGHT -> updateCursor(getCursorX() + 1, getCursorY());
        }
    }

    void changeDialog(Dialog dialog) throws IOException;

    void quit();
}
