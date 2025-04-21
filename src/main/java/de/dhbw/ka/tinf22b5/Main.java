package de.dhbw.ka.tinf22b5;

import de.dhbw.ka.tinf22b5.dialog.WelcomeDialog;
import de.dhbw.ka.tinf22b5.terminal.exception.TerminalHandlerException;
import de.dhbw.ka.tinf22b5.terminal.handler.BaseTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.render.BaseTerminalRenderingBuffer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, TerminalHandlerException {

        BaseTerminalHandler terminal = new BaseTerminalHandler(new WelcomeDialog());

        // TODO: remove
        BaseTerminalRenderingBuffer renderingBuffer = new BaseTerminalRenderingBuffer();
        renderingBuffer.scrollScreenUp();
        System.out.write(renderingBuffer.getBuffer());

        terminal.run();
    }
}