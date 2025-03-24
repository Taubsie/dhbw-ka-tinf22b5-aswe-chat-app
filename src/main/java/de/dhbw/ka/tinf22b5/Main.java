package de.dhbw.ka.tinf22b5;

import de.dhbw.ka.tinf22b5.dialog.WelcomeDialog;
import de.dhbw.ka.tinf22b5.terminal.exception.TerminalHandlerException;
import de.dhbw.ka.tinf22b5.terminal.handler.BaseTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.handler.lin.LinuxTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.handler.win.WindowsTerminalHandler;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BaseTerminalHandler terminal;
        // nicht schön aber funktioniert
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            terminal = new WindowsTerminalHandler(new WelcomeDialog());
        } else {
            terminal = new LinuxTerminalHandler(new WelcomeDialog());
        }

        try {
            terminal.init();
            terminal.attachShutdownHook();
        } catch (TerminalHandlerException e) {
            e.printStackTrace();
        }

        terminal.run();

        terminal.clearTerminal();

        // theoretically not needed when shutdown hook is attached
        try {
            terminal.deinit();
        } catch (TerminalHandlerException e) {
            e.printStackTrace();
        }
    }
}