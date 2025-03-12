package de.dhbw.ka.tinf22b5;

import de.dhbw.ka.tinf22b5.terminal.*;
import de.dhbw.ka.tinf22b5.terminal.lin.LinuxTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.win.WindowsTerminalHandler;
import de.dhbw.ka.tinf22b5.util.ProjectVersionUtil;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        TerminalHandler terminal;
        // nicht schön aber funktioniert
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            terminal = new WindowsTerminalHandler();
        } else {
            terminal = new LinuxTerminalHandler();
        }

        try {
            terminal.init();
            terminal.attachShutdownHook();
        } catch (TerminalHandlerException e) {
            e.printStackTrace();
        }

        boolean shouldStop = false;
        TerminalKeyParser terminalKeyParser = new TerminalKeyParser();
        while (!shouldStop) {
            TerminalKeyEvent event = terminalKeyParser.parseTerminalKeyInput(terminal.getChar());
            System.out.print("Pressed char: " + event.convertAllToUTF8String() + " " + event.getKeyType() + "\r\n");

            switch (event.getTerminalKey()) {
                case TerminalKey.TK_d, TerminalKey.TK_D -> System.out.print(terminal.getSize() + "\r\n");
                case TerminalKey.TK_q, TerminalKey.TK_Q -> shouldStop = true;
                case TerminalKey.TK_g, TerminalKey.TK_G -> System.out.write(new byte[] { 0x1b, '[', '6', 'n' });
                case TerminalKey.TK_v, TerminalKey.TK_V -> System.out.println("The current version is " + ProjectVersionUtil.getProjectVersion());
            }
        }

        // theoretically not needed when shutdown hook is attached
        try {
            terminal.deinit();
        } catch (TerminalHandlerException e) {
            e.printStackTrace();
        }
    }
}