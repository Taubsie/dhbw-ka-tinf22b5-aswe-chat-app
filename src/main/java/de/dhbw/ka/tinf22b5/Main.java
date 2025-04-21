package de.dhbw.ka.tinf22b5;

import de.dhbw.ka.tinf22b5.dialog.WelcomeDialog;
import de.dhbw.ka.tinf22b5.terminal.exception.TerminalHandlerException;
import de.dhbw.ka.tinf22b5.terminal.handler.BaseTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.handler.lin.LinuxTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.handler.win.WindowsTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.render.BaseTerminalRenderingBuffer;
import de.dhbw.ka.tinf22b5.util.OSUtil;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        BaseTerminalHandler terminal = switch (OSUtil.getOS()) {
            case WIN -> new WindowsTerminalHandler(new WelcomeDialog());
            case LINUX -> new LinuxTerminalHandler(new WelcomeDialog());
            default -> throw new RuntimeException("Unsupported OS: " + OSUtil.getOS());
        };

        try {
            terminal.init();
            terminal.attachShutdownHook();
        } catch (TerminalHandlerException e) {
            e.printStackTrace();
        }

        BaseTerminalRenderingBuffer renderingBuffer = new BaseTerminalRenderingBuffer();
        renderingBuffer.scrollScreenUp();
        System.out.write(renderingBuffer.getBuffer());

        terminal.run();

        // theoretically not needed when shutdown hook is attached
        try {
            terminal.deinit();
        } catch (TerminalHandlerException e) {
            e.printStackTrace();
        }
    }
}