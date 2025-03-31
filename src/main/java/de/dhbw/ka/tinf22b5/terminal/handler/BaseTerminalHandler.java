package de.dhbw.ka.tinf22b5.terminal.handler;

import de.dhbw.ka.tinf22b5.dialog.Dialog;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyParser;
import de.dhbw.ka.tinf22b5.terminal.render.BaseTerminalRenderingBuffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class BaseTerminalHandler implements TerminalHandler {
    private Dialog currentDialog;

    private int cursorX = 1;
    private int cursorY = 1;

    private boolean running = true;

    public BaseTerminalHandler(Dialog currentDialog) {
        this.currentDialog = currentDialog;
    }

    @Override
    public int getCursorX() {
        return cursorX;
    }

    @Override
    public int getCursorY() {
        return cursorY;
    }

    @Override
    public void setCursorX(int x) {
        this.cursorX = x;
    }

    @Override
    public void setCursorY(int y) {
        this.cursorY = y;
    }

    @Override
    public void changeDialog(@NotNull Dialog dialog) throws IOException {
        currentDialog = dialog;

        setCursorX(1);
        setCursorY(1);

        updateTerminal();
    }

    public void run() throws IOException {
        TerminalKeyParser terminalKeyParser = new TerminalKeyParser();

        while (running) {
            updateTerminal();

            TerminalKeyEvent event = terminalKeyParser.parseTerminalKeyInput(this, this.getChar());
            handleInput(event);
        }

        BaseTerminalRenderingBuffer renderingBuffer = new BaseTerminalRenderingBuffer();
        System.out.write(renderingBuffer.scrollScreenUp().getBuffer());
    }

    public void handleInput(TerminalKeyEvent event) throws IOException {
        currentDialog.handleInput(event, this);
    }

    public void updateTerminal() throws IOException {
        BaseTerminalRenderingBuffer renderingBuffer = new BaseTerminalRenderingBuffer();

        renderingBuffer.clearScreen();
        currentDialog.render(renderingBuffer);
        renderingBuffer.moveCursor(cursorX, cursorY);
        System.out.write(renderingBuffer.getBuffer());
    }

    public void quit() {
        running = false;
    }
}
