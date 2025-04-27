package de.dhbw.ka.tinf22b5.terminal.handler;

import de.dhbw.ka.tinf22b5.terminal.iohandler.IOTerminalFactory;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKey;
import de.dhbw.ka.tinf22b5.terminal.render.BaseTerminalScreen;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalScreen;
import de.dhbw.ka.tinf22b5.terminal.render.dialog.Dialog;
import de.dhbw.ka.tinf22b5.terminal.exception.TerminalHandlerException;
import de.dhbw.ka.tinf22b5.terminal.iohandler.IOTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyEvent;
import de.dhbw.ka.tinf22b5.terminal.key.TerminalKeyParser;
import de.dhbw.ka.tinf22b5.terminal.render.BaseTerminalRenderingBuffer;
import de.dhbw.ka.tinf22b5.terminal.render.TerminalRenderingBuffer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.Stack;

public class BaseTerminalHandler implements TerminalHandler {
    private Dialog currentDialog;

    private int cursorX = 1;
    private int cursorY = 1;

    private boolean running = true;

    private final IOTerminalHandler ioTerminalHandler;
    private final TerminalRenderingBuffer renderingBuffer;
    private final TerminalScreen terminalScreen;

    private final Stack<Dialog> dialogStack;

    public BaseTerminalHandler(Dialog currentDialog) throws TerminalHandlerException {
        this.currentDialog = currentDialog;

        this.ioTerminalHandler = IOTerminalFactory.createTerminalHandler();
        this.renderingBuffer = new BaseTerminalRenderingBuffer();
        this.terminalScreen = new BaseTerminalScreen();

        this.dialogStack = new Stack<>();
        this.dialogStack.push(this.currentDialog);
    }

    @Override
    public void changeDialog(@NotNull Dialog dialog) throws IOException {
        currentDialog = dialog;

        updateTerminal();
    }

    @Override
    public void pushDialog(Dialog dialog) throws IOException {
        dialogStack.push(currentDialog);

        currentDialog = dialog;

        updateTerminal();
    }

    @Override
    public void popDialog() throws IOException {
        currentDialog = dialogStack.pop();

        updateTerminal();
    }

    public void run() throws IOException, TerminalHandlerException {
        this.ioTerminalHandler.init();

        // TODO: make cleaner
        System.out.write(renderingBuffer.clear().alternateScreenEnable().setCursorVisible(false).getBuffer());

        TerminalKeyParser terminalKeyParser = new TerminalKeyParser();

        while (running) {
            updateTerminal();

            TerminalKeyEvent event = terminalKeyParser.parseTerminalKeyInput(ioTerminalHandler.getChar());
            handleInput(this, event);
        }

        // TODO: make cleaner
        renderingBuffer.clear();
        renderingBuffer.alternateScreenDisable();
        renderingBuffer.setCursorVisible(true);
        renderingBuffer.resetGraphicsModes();
        System.out.write(renderingBuffer.getBuffer());
    }

    public void handleInput(TerminalHandler terminalHandler, TerminalKeyEvent event) throws IOException {
        if (currentDialog.handleInput(terminalHandler, event))
            return;

        switch (event.getTerminalKey()) {
            case TerminalKey.TK_Q:
            case TerminalKey.TK_q:
            case TerminalKey.TK_CTRL_C:
                this.quit();
        }
    }

    public void updateTerminal() throws IOException {
        Dimension terminalSize = this.ioTerminalHandler.getSize();

        terminalScreen.clear();
        terminalScreen.doResize(terminalSize);

        currentDialog.setStartPoint(new Point(0, 0));
        currentDialog.setSize(terminalSize);
        currentDialog.layout();
        currentDialog.render(terminalScreen);

        renderingBuffer.clear();
        renderingBuffer.resetGraphicsModes();
        renderingBuffer.scrollScreenUp();
        terminalScreen.renderIntoBuffer(renderingBuffer);
        renderingBuffer.moveCursor(cursorX, cursorY);
        System.out.write(renderingBuffer.getBuffer());
    }

    public void quit() {
        running = false;

        try {
            this.ioTerminalHandler.deinit();
        } catch (TerminalHandlerException _) {
            // ignore closing errors
        }
    }
}
