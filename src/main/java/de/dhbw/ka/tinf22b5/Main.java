package de.dhbw.ka.tinf22b5;

import de.dhbw.ka.tinf22b5.terminal.*;
import de.dhbw.ka.tinf22b5.terminal.lin.LinuxTerminalHandler;
import de.dhbw.ka.tinf22b5.terminal.win.WindowsTerminalHandler;
import de.dhbw.ka.tinf22b5.util.ProjectVersionUtil;

import java.io.IOException;
import java.util.Arrays;

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

        int x = 1;
        int y = 1;
        while (!shouldStop) {
            TerminalKeyEvent event = terminalKeyParser.parseTerminalKeyInput(terminal.getChar());

            // clear screen
            System.out.write(new byte[] { 0x1b, '[', '2', 'J', 0x1b, '[', 'H' });
            System.out.print("Pressed char: " + event.convertAllToUTF8String() + " " + event.getKeyType() + " " + Arrays.toString(event.getUtf8Chars()) + "\r\n");

            switch (event.getTerminalKey()) {
                case TerminalKey.TK_e, TerminalKey.TK_E -> System.out.print(terminal.getSize() + "\r\n");
                case TerminalKey.TK_q, TerminalKey.TK_Q -> shouldStop = true;
                case TerminalKey.TK_UP, TerminalKey.TK_W, TerminalKey.TK_w -> y = Math.min(terminal.getSize().height, Math.max(1, y - 1));
                case TerminalKey.TK_DOWN, TerminalKey.TK_S, TerminalKey.TK_s -> y = Math.min(terminal.getSize().height, Math.max(1, y + 1));
                case TerminalKey.TK_RIGHT, TerminalKey.TK_D, TerminalKey.TK_d -> x = Math.min(terminal.getSize().width, Math.max(1, x + 1));
                case TerminalKey.TK_LEFT, TerminalKey.TK_A, TerminalKey.TK_a -> x = Math.min(terminal.getSize().width, Math.max(1, x - 1));
                case TerminalKey.TK_v, TerminalKey.TK_V -> System.out.println("The current version is " + ProjectVersionUtil.getProjectVersion());
            }

            System.out.write( (((char) 0x1b) + "[%d;%dH".formatted(y, x)).getBytes());
        }
        // clear screen
        System.out.write(new byte[] { 0x1b, '[', '2', 'J', 0x1b, '[', 'H' });

        // theoretically not needed when shutdown hook is attached
        try {
            terminal.deinit();
        } catch (TerminalHandlerException e) {
            e.printStackTrace();
        }
    }
}