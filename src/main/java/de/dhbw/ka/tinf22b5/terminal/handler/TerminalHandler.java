package de.dhbw.ka.tinf22b5.terminal.handler;

import de.dhbw.ka.tinf22b5.dialog.Dialog;
import de.dhbw.ka.tinf22b5.terminal.CursorDirection;
import de.dhbw.ka.tinf22b5.terminal.exception.TerminalHandlerException;

import java.awt.*;
import java.io.IOException;

public interface TerminalHandler {

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

    int getCursorX();

    int getCursorY();

    void setCursorX(int x);

    void setCursorY(int y);

    default void updateCursor(int x, int y) throws IOException {
        x = Math.min(getSize().width, Math.max(1, x));
        y = Math.min(getSize().height, Math.max(1, y));

        setCursorX(x);
        setCursorY(y);

        // Sets the cursor position
        System.out.write( (((char) 0x1b) + "[%d;%dH".formatted(y, x)).getBytes());
    }
    
    default void moveCursor(CursorDirection direction) throws IOException {
        switch (direction) {
            case UP -> updateCursor(getCursorX(), getCursorY() - 1);
            case DOWN -> updateCursor(getCursorX(), getCursorY() + 1);
            case LEFT -> updateCursor(getCursorX() - 1, getCursorY());
            case RIGHT -> updateCursor(getCursorX() + 1, getCursorY());
        }
    }

    default void clearTerminal() throws IOException {
        System.out.write(new byte[] { 0x1b, '[', '2', 'J', 0x1b, '[', 'H' });
    }

    void changeDialog(Dialog dialog) throws IOException;

    void quit();
}